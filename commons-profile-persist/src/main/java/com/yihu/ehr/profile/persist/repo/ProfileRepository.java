package com.yihu.ehr.profile.persist.repo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.data.hbase.CellBundle;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.core.commons.*;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * 健康档案加载器. 可以根据健康档案ID或与之关联的事件ID加载档案.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:20
 */
@Service
public class ProfileRepository {
    @Autowired
    HBaseDao hbaseDao;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CacheReader cacheReader;

    @Autowired
    StdKeySchema keySchema;

    /**
     * 获取档案
     *
     * @param profileId
     * @return
     */
    public StructedProfile findOne(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        CellBundle profileCellBundle = new CellBundle();
        profileCellBundle.addRow(profileId);

        Object[] results = hbaseDao.get(ProfileUtil.Table, profileCellBundle);
        if (results.length != 1) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil((Result) results[0]);

        String cardId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CardId);
        String orgCode = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.OrgCode);
        String patientId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.PatientId);
        String eventNo = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventNo);
        String eventDate = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventDate);
        String eventType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventType);
        String profileType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.ProfileType);
        String summary = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.Summary);
        String demographicId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DemographicId);
        String createDate = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CreateDate);
        String cdaVersion = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CdaVersion);
        String dataSets = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DataSets);

        profileType = StringUtils.isEmpty(profileType) ? Integer.toString(ProfileType.Structured.getType()) : profileType;
        ProfileType pType = ProfileType.valueOf(profileType);
        EventType eType = EventType.valueOf(eventType);

        StructedProfile profile = ProfileGenerator.generate(ProfileType.valueOf(profileType));
        profile.setId(profileId);
        profile.setCardId(cardId);
        profile.setOrgCode(orgCode);
        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setEventDate(DateTimeUtils.utcDateTimeParse(eventDate));
        profile.setEventType(eType);
        profile.setProfileType(pType);
        profile.setSummary(summary);
        profile.setDemographicId(demographicId);
        profile.setCreateDate(DateTimeUtils.utcDateTimeParse(createDate));
        profile.setCdaVersion(cdaVersion);

        // 加载数据集列表
        JsonNode root = objectMapper.readTree(dataSets);
        Iterator<String> iterator = root.fieldNames();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            String[] rowKeys = root.get(dataSetCode).asText().split(",");

            if (loadStdDataSet || loadOriginDataSet) {
                if (loadStdDataSet) {
                    if (!dataSetCode.contains(DataSetUtil.OriginDataSetFlag)) {
                        Pair<String, StdDataSet> pair = findDataSet(cdaVersion, dataSetCode, pType, rowKeys);
                        profile.insertDataSet(pair.getLeft(), pair.getRight());
                    }
                }
                if (loadOriginDataSet) {
                    if (dataSetCode.contains(DataSetUtil.OriginDataSetFlag)) {
                        Pair<String, StdDataSet> pair = findDataSet(cdaVersion, dataSetCode, pType, rowKeys);
                        profile.insertDataSet(pair.getLeft(), pair.getRight());
                    }
                }
            } else {
                Pair<String, StdDataSet> pair = findDataSetIndices(cdaVersion, dataSetCode, rowKeys);
                profile.insertDataSet(pair.getLeft(), pair.getRight());
            }
        }

        return profile;
    }

    /**
     * 获取数据集。
     *
     * @param dataSetCode 欲加载的数据集代码
     * @param rowkeys     数据集rowkey列表
     * @return
     * @throws IOException
     */
    public Pair<String, StdDataSet> findDataSet(String cdaVersion,
                                                String dataSetCode,
                                                ProfileType profileType,
                                                String[] rowkeys) throws IOException {
        StdDataSet dataSet = profileType == ProfileType.Link ? new LinkDataSet() : new StdDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        CellBundle dataSetBundle = new CellBundle();
        for (String rowkey : rowkeys) {
            dataSetBundle.addFamily(rowkey, DataSetFamily.Basic);
            dataSetBundle.addFamily(rowkey, DataSetFamily.MetaData);
            dataSetBundle.addFamily(rowkey, DataSetFamily.Extension);
        }

        Object[] results = hbaseDao.get(dataSetCode, dataSetBundle);
        extractResultToDataSet(dataSet, results);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 获取部分数据集。
     *
     * @param version
     * @param dataSetCode
     * @param rowkeys
     * @param metaDataCodes
     * @return
     * @throws IOException
     */
    public Pair<String, StdDataSet> findDataSet(String version,
                                                String dataSetCode,
                                                ProfileType profileType,
                                                Set<String> rowkeys,
                                                String[] metaDataCodes) throws IOException {
        List<String> metaDataCode = new ArrayList<>(metaDataCodes.length);
        for (int i = 0; i < metaDataCodes.length; ++i) {
            Long dictId = cacheReader.read(keySchema.metaDataDict(version, dataSetCode, metaDataCodes[i]));
            String type = cacheReader.read(keySchema.metaDataType(version, dataSetCode, metaDataCodes[i]));
            type = type == null ? "S1" : type;
            if (dictId == null || dictId == 0) {
                metaDataCode.add(QualifierTranslator.hBaseQualifier(metaDataCodes[i], type));
            } else if (dictId > 0) {
                String[] temp = QualifierTranslator.splitMetaData(metaDataCodes[i]);
                metaDataCode.add(QualifierTranslator.hBaseQualifier(temp[0], type));
                metaDataCode.add(QualifierTranslator.hBaseQualifier(temp[1], type));
            }
        }

        CellBundle dataSetBundle = new CellBundle();
        for (String rowkey : rowkeys) {
            dataSetBundle.addColumns(rowkey, DataSetFamily.Basic, DataSetFamily.getColumns(DataSetFamily.Basic));
            dataSetBundle.addColumns(rowkey, DataSetFamily.MetaData, metaDataCode.toArray());
            dataSetBundle.addColumns(rowkey, DataSetFamily.Extension, dataSetCode);
        }

        StdDataSet dataSet = profileType == ProfileType.Link ? new LinkDataSet() : new StdDataSet();
        dataSet.setCdaVersion(version);
        dataSet.setCode(dataSetCode);

        Object[] results = hbaseDao.get(dataSetCode, dataSetBundle);
        extractResultToDataSet(dataSet, results);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    private void extractResultToDataSet(StdDataSet dataSet, Object[] results){
        for (Object item : results) {
            DataRecord record = new DataRecord();

            // 数据集内容
            ResultUtil result = new ResultUtil(item);
            List<Cell> cellList = result.listCells();

            for (Cell cell : cellList) {
                String qualifier = result.getCellQualifier(cell);
                if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    qualifier = qualifier.substring(0, qualifier.lastIndexOf('_'));
                    record.putMetaData(qualifier, result.getCellValue(cell));
                }

                if (qualifier.equals(dataSet.getCode()) && dataSet instanceof LinkDataSet) {
                    ((LinkDataSet) dataSet).setUrl(result.getCellValue(cell));
                }
            }

            dataSet.addRecord(result.getRowKey(), record);
        }
    }

    /**
     * 只获取数据集索引，不加载数据集内容，提高效率。
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param rowKeys
     * @return
     */
    public Pair<String, StdDataSet> findDataSetIndices(String cdaVersion,
                                                       String dataSetCode,
                                                       String[] rowKeys) {
        StdDataSet dataSet = new StdDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        for (String rowKey : rowKeys) {
            dataSet.addRecord(rowKey, null);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    public void save(StructedProfile profile) throws IOException {
        CellBundle profileBundle = new CellBundle();
        profileBundle.addValues(profile.getId(), ProfileFamily.Basic, ProfileUtil.getBasicFamilyCellMap(profile));

        // 先存档案
        hbaseDao.saveOrUpdate(ProfileUtil.Table, profileBundle);

        if (profile instanceof StructedProfile) {
            for (StdDataSet dataSet : profile.getDataSets()) {

                CellBundle dataSetBundle = new CellBundle();
                for (String rowkey : dataSet.getRecordKeys()) {
                    dataSetBundle.addValues(rowkey, DataSetFamily.Basic, DataSetUtil.getBasicFamilyMap(dataSet));
                    dataSetBundle.addValues(rowkey, DataSetFamily.MetaData, DataSetUtil.getMetaDataFamilyMap(dataSet));

                    DataRecord record = dataSet.getRecord(rowkey);
                    String[][] hbDataArray = DataSetUtil.metaDataToColumns(record);

                    // Basic列族
                    hbaseDao.batchInsert(rowkey,
                            DataSetUtil.Family.Basic.toString(),
                            DataSetUtil.getQualifiers(DataSetUtil.Family.Basic),
                            new String[]{
                                    structedProfile.getId(),
                                    dataSet.getCdaVersion(),
                                    dataSet.getOrgCode(),
                                    DateTimeUtils.utcDateTimeFormat(new Date())
                            });

                    // MetaData列族
                    hbaseDao.batchInsert(rowkey,
                            DataSetUtil.Family.MetaData.toString(),
                            hbDataArray[0],
                            hbDataArray[1]);
                }

                hbaseDao.saveOrUpdate(dataSet.getCode(), dataSetBundle);

                // Extension列族
                if (dataSet instanceof LinkDataSet) {
                    LinkDataSet linkDataSet = (LinkDataSet) dataSet;
                    hbaseDao.batchInsert(dataSet.getFirstRowkey(),
                            DataSetUtil.Family.Extension.toString(),
                            new String[]{
                                    dataSet.getCode()
                            },
                            new String[]{
                                    linkDataSet.getUrl()
                            });
                }
            }
        }
    }
}
