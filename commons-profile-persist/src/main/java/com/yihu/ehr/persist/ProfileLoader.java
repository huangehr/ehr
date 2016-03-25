package com.yihu.ehr.persist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.schema.CachedMetaData;
import com.yihu.ehr.schema.StdRedisCacheAccessor;
import com.yihu.ehr.schema.StdObjectQualifierTranslator;
import com.yihu.ehr.data.HBaseClient;
import com.yihu.ehr.data.ResultWrapper;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.profile.ProfileTableOptions;
import com.yihu.ehr.util.DateFormatter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.yihu.ehr.profile.ProfileTableOptions.*;

/**
 * 健康档案加载器. 可以根据健康档案ID或与之关联的事件ID加载档案.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:20
 */
public class ProfileLoader {
    @Autowired
    HBaseClient hbaseClient;

    @Autowired
    ObjectMapper objectMapper;

    public ProfileLoader() {
    }

    /**
     * 根据档案ID加载档案数据.
     *
     * @param archiveId
     * @param loadStdDataSet
     * @param loadOriginDataSet
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public Profile load(String archiveId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        return loadArchive(archiveId, loadStdDataSet, loadOriginDataSet);
    }

    /**
     * 加载档案. 返回的map中key为健康事件id.
     *
     * @param archiveId
     * @return
     */
    public Profile loadArchive(String archiveId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        ResultWrapper record = hbaseClient.getResultAsWrapper(ProfileTableOptions.ArchiveTable, archiveId);
        if (record.getResult().toString().equals("keyvalues=NONE")) throw new RuntimeException("Profile not found.");

        String cardId = record.getValueAsString(FamilyBasic, AcCardId);
        String orgCode = record.getValueAsString(FamilyBasic, AcOrgCode);
        String patientId = record.getValueAsString(FamilyBasic, AcPatientId);
        String eventNo = record.getValueAsString(FamilyBasic, AcEventNo);
        String eventDate = record.getValueAsString(FamilyBasic, AcEventDate);
        String summary = record.getValueAsString(FamilyBasic, AcSummary);
        String demographicId = record.getValueAsString(FamilyBasic, AcDemographicId);
        String createDate = record.getValueAsString(FamilyBasic, AcCreateDate);
        String cdaVersion = record.getValueAsString(FamilyBasic, AcInnerVersion);
        String dataSets = record.getValueAsString(FamilyBasic, AcDataSets);

        Profile profile = new Profile();
        profile.setId(archiveId);
        profile.setCardId(cardId);
        profile.setOrgCode(orgCode);
        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setEventDate(DateFormatter.utcDateTimeParse(eventDate));
        profile.setSummary(summary);
        profile.setDemographicId(demographicId);
        profile.setCreateDate(DateFormatter.utcDateTimeParse(createDate));
        profile.setCdaVersion(cdaVersion);

        // 加载数据集列表
        JsonNode root = objectMapper.readTree(dataSets);
        Iterator<String> iterator = root.fieldNames();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            String[] rowKeys = root.get(dataSetCode).asText().split(",");

            if (loadStdDataSet || loadOriginDataSet){
                if (loadStdDataSet) {
                    if (!dataSetCode.contains(StdObjectQualifierTranslator.OriginDataSetFlag)) {
                        Pair<String, ProfileDataSet> pair = loadFullDataSet(cdaVersion, dataSetCode, rowKeys);
                        profile.addDataSet(pair.getLeft(), pair.getRight());
                    }
                }
                if (loadOriginDataSet) {
                    if (dataSetCode.contains(StdObjectQualifierTranslator.OriginDataSetFlag)){
                        Pair<String, ProfileDataSet> pair = loadFullDataSet(cdaVersion, dataSetCode, rowKeys);
                        profile.addDataSet(pair.getLeft(), pair.getRight());
                    }
                }
            } else {
                Pair<String, ProfileDataSet> pair = loadOnlyIndexedDataSet(cdaVersion, dataSetCode, rowKeys);
                profile.addDataSet(pair.getLeft(), pair.getRight());
            }
        }

        return profile;
    }

    /**
     * 加载数据集全部数据。
     *
     * @param dataSetCode
     * @param rowKeys
     * @return
     * @throws IOException
     */
    public Pair<String, ProfileDataSet> loadFullDataSet(String cdaVersion,
                                                        String dataSetCode,
                                                        String[] rowKeys) throws IOException {
        ProfileDataSet dataSet = new ProfileDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        Result[] results = (Result[]) hbaseClient.getRecords(
                dataSetCode,
                rowKeys,
                new String[]{ProfileTableOptions.FamilyBasic, ProfileTableOptions.FamilyMetaData}
        );

        for (Result result : results){
            Map<String, String> record = new HashMap<>();

            // 数据集内容
            List<Cell> cellList = result.listCells();
            if (cellList == null) continue;

            for (Cell cell : cellList) {
                String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));

                if (qualifier.equals(ProfileTableOptions.DScPatientId)) {
                    dataSet.setPatientId(value);
                } else if (qualifier.equals(ProfileTableOptions.DScEventNo)) {
                    dataSet.setEventNo(value);
                } else if (qualifier.equals(ProfileTableOptions.DScOrgCode)) {
                    dataSet.setOrgCode(value);
                } else if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    record.put(qualifier, value);
                }
            }

            String rowKey = Bytes.toString(result.getRow());
            dataSet.addRecord(rowKey, record);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 只加创建并加载数据集索引，不加载数据集内容。提高效率。
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param rowKeys
     * @return
     */
    public Pair<String, ProfileDataSet> loadOnlyIndexedDataSet(String cdaVersion,
                                                               String dataSetCode,
                                                               String[] rowKeys){
        ProfileDataSet dataSet = new ProfileDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        for (String rowKey : rowKeys) {
            dataSet.addRecord(rowKey, null);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 加载部分数据集。
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param rowKeys
     * @param innerCodes
     * @return
     * @throws IOException
     */
    public Pair<String, ProfileDataSet> loadPartialDataSet(String cdaVersion,
                                                           String dataSetCode,
                                                           Set<String> rowKeys,
                                                           String[] innerCodes) throws IOException {
        List<String> metaDataInnerCode = new ArrayList<>(innerCodes.length);
        for (int i = 0; i < innerCodes.length; ++i) {
            CachedMetaData metaData = StdRedisCacheAccessor.getMetaData(cdaVersion, dataSetCode, innerCodes[i]);
            if (metaData == null) {
                continue;
            } else if (metaData.dictId == 0) {
                metaDataInnerCode.add(StdObjectQualifierTranslator.toHBaseQualifier(innerCodes[i], metaData.type));
            } else if (metaData.dictId > 0) {
                String[] temp = StdObjectQualifierTranslator.splitInnerCodeAsCodeValue(innerCodes[i]);
                metaDataInnerCode.add(StdObjectQualifierTranslator.toHBaseQualifier(temp[0], metaData.type));
                metaDataInnerCode.add(StdObjectQualifierTranslator.toHBaseQualifier(temp[1], metaData.type));
            }
        }

        ProfileDataSet dataSet = new ProfileDataSet();
        Result[] results = hbaseClient.getPartialRecords(dataSetCode,
                rowKeys.toArray(new String[rowKeys.size()]),
                new String[]{ProfileTableOptions.FamilyBasic, ProfileTableOptions.FamilyMetaData},
                new String[][]{
                        new String[]{
                                ProfileTableOptions.DScPatientId,
                                ProfileTableOptions.DScEventNo,
                                ProfileTableOptions.DScOrgCode,
                                ProfileTableOptions.DScInnerVersion},
                        metaDataInnerCode.toArray(new String[metaDataInnerCode.size()])});

        for (Result result : results) {
            List<Cell> cellList = result.listCells();
            if (cellList == null) continue;

            Map<String, String> record = new HashMap<>();
            for (Cell cell : cellList) {
                String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));

                if (qualifier.equals(ProfileTableOptions.DScArchiveId)) {
                    // pass
                } else if (qualifier.equals(ProfileTableOptions.DScPatientId)) {
                    dataSet.setPatientId(value);
                } else if (qualifier.equals(ProfileTableOptions.DScEventNo)) {
                    dataSet.setEventNo(value);
                } else if (qualifier.equals(ProfileTableOptions.DScOrgCode)) {
                    dataSet.setOrgCode(value);
                } else if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    record.put(qualifier.substring(0, qualifier.lastIndexOf("_")), value);
                }
            }
            dataSet.addRecord(Bytes.toString(result.getRow()), record);
        }

        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }
}
