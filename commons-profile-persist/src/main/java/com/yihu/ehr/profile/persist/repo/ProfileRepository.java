package com.yihu.ehr.profile.persist.repo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.data.hadoop.HBaseClient;
import com.yihu.ehr.data.hadoop.ResultWrapper;
import com.yihu.ehr.profile.core.commons.*;
import com.yihu.ehr.profile.core.lightweight.LightWeightDataSet;
import com.yihu.ehr.profile.core.nostructured.NoStructuredProfile;
import com.yihu.ehr.profile.core.structured.FullWeightDataSet;
import com.yihu.ehr.profile.core.structured.FullWeightProfile;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.yihu.ehr.profile.core.commons.ProfileTableOptions.BasicQualifier;
import static com.yihu.ehr.profile.core.commons.ProfileTableOptions.Family;

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
    HBaseClient hbaseClient;

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
    public FullWeightProfile findOne(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        ResultWrapper record = hbaseClient.getResultAsWrapper(ProfileTableOptions.Table, profileId);
        if (record.getResult().toString().equals("keyvalues=NONE")) throw new RuntimeException("Profile not found.");

        String cardId = record.getValueAsString(Family.Basic.toString(), BasicQualifier.CardId.toString());
        String orgCode = record.getValueAsString(Family.Basic.toString(), BasicQualifier.OrgCode.toString());
        String patientId = record.getValueAsString(Family.Basic.toString(), BasicQualifier.PatientId.toString());
        String eventNo = record.getValueAsString(Family.Basic.toString(), BasicQualifier.EventNo.toString());
        String eventDate = record.getValueAsString(Family.Basic.toString(), BasicQualifier.EventDate.toString());
        String summary = record.getValueAsString(Family.Basic.toString(), BasicQualifier.Summary.toString());
        String demographicId = record.getValueAsString(Family.Basic.toString(), BasicQualifier.DemographicId.toString());
        String createDate = record.getValueAsString(Family.Basic.toString(), BasicQualifier.CreateDate.toString());
        String cdaVersion = record.getValueAsString(Family.Basic.toString(), BasicQualifier.CdaVersion.toString());
        String dataSets = record.getValueAsString(Family.Basic.toString(), BasicQualifier.DataSets.toString());

        FullWeightProfile fullWeightProfile = new FullWeightProfile();
        fullWeightProfile.setId(profileId);
        fullWeightProfile.setCardId(cardId);
        fullWeightProfile.setOrgCode(orgCode);
        fullWeightProfile.setPatientId(patientId);
        fullWeightProfile.setEventNo(eventNo);
        fullWeightProfile.setEventDate(DateTimeUtils.utcDateTimeParse(eventDate));
        fullWeightProfile.setSummary(summary);
        fullWeightProfile.setDemographicId(demographicId);
        fullWeightProfile.setCreateDate(DateTimeUtils.utcDateTimeParse(createDate));
        fullWeightProfile.setCdaVersion(cdaVersion);

        // 加载数据集列表
        JsonNode root = objectMapper.readTree(dataSets);
        Iterator<String> iterator = root.fieldNames();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            String[] rowKeys = root.get(dataSetCode).asText().split(",");

            if (loadStdDataSet || loadOriginDataSet) {
                if (loadStdDataSet) {
                    if (!dataSetCode.contains(DataSetTableOption.OriginDataSetFlag)) {
                        Pair<String, FullWeightDataSet> pair = findDataSet(cdaVersion, dataSetCode, rowKeys);
                        fullWeightProfile.addFullWeightDataSet(pair.getLeft(), pair.getRight());
                    }
                }
                if (loadOriginDataSet) {
                    if (dataSetCode.contains(DataSetTableOption.OriginDataSetFlag)) {
                        Pair<String, FullWeightDataSet> pair = findDataSet(cdaVersion, dataSetCode, rowKeys);
                        fullWeightProfile.addFullWeightDataSet(pair.getLeft(), pair.getRight());
                    }
                }
            } else {
                Pair<String, FullWeightDataSet> pair = findDataSetIndices(cdaVersion, dataSetCode, rowKeys);
                fullWeightProfile.addFullWeightDataSet(pair.getLeft(), pair.getRight());
            }
        }

        return fullWeightProfile;
    }

    /**
     * 获取数据集。
     *
     * @param dataSetCode 欲加载的数据集代码
     * @param rowKeys     数据集rowkey列表
     * @return
     * @throws IOException
     */
    public Pair<String, FullWeightDataSet> findDataSet(String cdaVersion,
                                                       String dataSetCode,
                                                       String[] rowKeys) throws IOException {
        FullWeightDataSet dataSet = new FullWeightDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        Object[] results = hbaseClient.getRecords(
                dataSetCode,
                rowKeys,
                new String[]{DataSetTableOption.Family.Basic.toString(),
                        DataSetTableOption.Family.MetaData.toString()}
        );

        for (Object obj : results) {
            Map<String, String> record = new HashMap<>();

            // 数据集内容
            Result result = (Result) obj;
            List<Cell> cellList = result.listCells();
            if (cellList == null) continue;

            for (Cell cell : cellList) {
                String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));

                if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    record.put(qualifier, value);
                }
            }

            String rowKey = Bytes.toString(result.getRow());
            dataSet.addRecord(rowKey, record);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 获取部分数据集。
     *
     * @param version
     * @param dataSetCode
     * @param rowKeys
     * @param innerCodes
     * @return
     * @throws IOException
     */
    public Pair<String, FullWeightDataSet> findDataSet(String version,
                                                       String dataSetCode,
                                                       Set<String> rowKeys,
                                                       String[] innerCodes) throws IOException {
        List<String> metaDataCode = new ArrayList<>(innerCodes.length);
        for (int i = 0; i < innerCodes.length; ++i) {
            Long dictId = cacheReader.read(keySchema.metaDataDict(version, dataSetCode, innerCodes[i]));
            String type = cacheReader.read(keySchema.metaDataType(version, dataSetCode, innerCodes[i]));
            type = type == null ? "S1" : type;
            if (dictId == null || dictId == 0) {
                metaDataCode.add(QualifierTranslator.hBaseQualifier(innerCodes[i], type));
            } else if (dictId > 0) {
                String[] temp = QualifierTranslator.splitMetaData(innerCodes[i]);
                metaDataCode.add(QualifierTranslator.hBaseQualifier(temp[0], type));
                metaDataCode.add(QualifierTranslator.hBaseQualifier(temp[1], type));
            }
        }

        FullWeightDataSet dataSet = new FullWeightDataSet();
        Result[] results = hbaseClient.getPartialRecords(dataSetCode,
                rowKeys.toArray(new String[rowKeys.size()]),
                new String[]{
                        DataSetTableOption.Family.Basic.toString(),
                        DataSetTableOption.Family.MetaData.toString()
                },
                new String[][]{
                        DataSetTableOption.getQualifiers(DataSetTableOption.Family.Basic),
                        metaDataCode.toArray(new String[metaDataCode.size()])});

        for (Result result : results) {
            List<Cell> cellList = result.listCells();
            if (cellList == null) continue;

            Map<String, String> record = new HashMap<>();
            for (Cell cell : cellList) {
                String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));

                if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    record.put(qualifier.substring(0, qualifier.lastIndexOf("_")), value);
                }
            }
            dataSet.addRecord(Bytes.toString(result.getRow()), record);
        }

        dataSet.setCdaVersion(version);
        dataSet.setCode(dataSetCode);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 只获取数据集索引，不加载数据集内容，提高效率。
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param rowKeys
     * @return
     */
    public Pair<String, FullWeightDataSet> findDataSetIndices(String cdaVersion,
                                                              String dataSetCode,
                                                              String[] rowKeys) {
        FullWeightDataSet dataSet = new FullWeightDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        for (String rowKey : rowKeys) {
            dataSet.addRecord(rowKey, null);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }


    /**
     * 保存全量级档案。
     *
     * @param structuredProfileModel
     * @throws IOException
     */
    public void saveStructuredProfileModel(StructuredProfileModel structuredProfileModel) throws IOException {
        // 先存档案
        ProfileType profileType = structuredProfileModel.getProfileType();
        hbaseClient.insertRecord(ProfileTableOptions.Table,
                structuredProfileModel.getId(),
                ProfileTableOptions.Family.Basic.toString(),
                ProfileTableOptions.getQualifiers(ProfileTableOptions.Family.Basic),
                new String[]{
                        structuredProfileModel.getCardId(),
                        structuredProfileModel.getOrgCode(),
                        structuredProfileModel.getPatientId(),
                        structuredProfileModel.getEventNo(),
                        DateTimeUtils.utcDateTimeFormat(structuredProfileModel.getEventDate()),
                        structuredProfileModel.getSummary(),
                        structuredProfileModel.getDemographicId() == null ? "" : structuredProfileModel.getDemographicId(),
                        DateTimeUtils.utcDateTimeFormat(structuredProfileModel.getCreateDate()),
                        profileType == ProfileType.FullWeight ? structuredProfileModel.getFullWeightDataSetsAsString() : structuredProfileModel.getLightWeightDataSetsAsString(),
                        structuredProfileModel.getCdaVersion()
                });

        // 数据集
        Set<String> tableSet = profileType == ProfileType.FullWeight ? structuredProfileModel.getFullWeightDataTables() : structuredProfileModel.getLightWeightDataSetTables();
        if (profileType == ProfileType.Lightweight) {
            //保存轻量级档案数据集
            InsertDataSet2ExtensionFamliy(tableSet, structuredProfileModel);
            //// TODO: 2016/4/22 保存到拓展列族中去

        } else if (profileType == ProfileType.FullWeight) {
            //保存全量级档案数据集
            InsertDataSet2DataSetFamliy(tableSet, structuredProfileModel);
        }

    }


    /**
     * 非结构化档案上传到hbase
     *
     * @param noStructuredProfile
     * @throws IOException
     * @throws ParseException
     */
    public void saveUnStructuredProfile(NoStructuredProfile noStructuredProfile) throws IOException, ParseException {
        // 先存档案
        hbaseClient.insertRecord(ProfileTableOptions.Table,
                noStructuredProfile.getId(),
                ProfileTableOptions.Family.Basic.toString(),
                ProfileTableOptions.getQualifiers(ProfileTableOptions.Family.Basic),
                new String[]{
                        noStructuredProfile.getCardId(),
                        noStructuredProfile.getOrgCode(),
                        noStructuredProfile.getPatientId(),
                        noStructuredProfile.getEventNo(),
                        DateTimeUtils.utcDateTimeFormat(noStructuredProfile.getEventDate()),
                        noStructuredProfile.getSummary(),
                        noStructuredProfile.getDemographicId() == null ? "" : noStructuredProfile.getDemographicId(),
                        DateTimeUtils.utcDateTimeFormat(noStructuredProfile.getCreateDate()),
                        //非结构化档案暂时不保存数据集信息
                        //unStructuredProfile.getDataSetsAsString(),
                        "",
                        noStructuredProfile.getCdaVersion()
                });

        // 非结构化档案文档解析

        hbaseClient.insertRecord(DocumentTableOption.Table,
                noStructuredProfile.getId(),
                DocumentTableOption.Family.Document.toString(),
                DocumentTableOption.getQualifiers(DocumentTableOption.Family.Document),
                new String[]{
                        noStructuredProfile.getOrgCode(),
                        noStructuredProfile.getPatientId(),
                        noStructuredProfile.getEventNo(),
                        DateTimeUtils.utcDateTimeFormat(noStructuredProfile.getEventDate()),  //日期格式化
                        noStructuredProfile.getCdaVersion(),
                        objectMapper.writeValueAsString(noStructuredProfile.getNoStructuredDocumentList())
                });


    }


    /**
     * 结构化档案数据集上传
     *
     * @param tableSet
     * @param structuredProfileModel
     * @throws IOException
     */
    private void InsertDataSet2DataSetFamliy(Set<String> tableSet, StructuredProfileModel structuredProfileModel) throws IOException {
        for (String tableName : tableSet) {
            FullWeightDataSet fullWeightDataSet = structuredProfileModel.getFullWeightData(tableName);

            hbaseClient.beginBatchInsert(tableName, false);
            for (String key : fullWeightDataSet.getRecordKeys()) {
                Map<String, String> record = fullWeightDataSet.getRecord(key);
                String[][] hbDataArray = DataSetTableOption.metaDataToQualifier(record);

                // 所属档案ID，标准版本号
                hbaseClient.batchInsert(key,
                        DataSetTableOption.Family.Basic.toString(),
                        DataSetTableOption.getQualifiers(DataSetTableOption.Family.Basic),
                        new String[]{
                                structuredProfileModel.getId(),
                                fullWeightDataSet.getCdaVersion(),
                                DateTimeUtils.utcDateTimeFormat(new Date())
                        });

                // 数据元
                hbaseClient.batchInsert(key,
                        DataSetTableOption.Family.MetaData.toString(),
                        hbDataArray[0],
                        hbDataArray[1]);
            }

            hbaseClient.endBatchInsert();
        }
    }


    /**
     * 轻量级档案数据集上传
     *
     * @param tableSet
     * @param structuredProfileModel
     * @throws IOException
     */
    public void InsertDataSet2ExtensionFamliy(Set<String> tableSet, StructuredProfileModel structuredProfileModel) throws IOException {
        for (String tableName : tableSet) {
            LightWeightDataSet lightWeightDataSet = structuredProfileModel.getLightWeightDataSet(tableName);

            hbaseClient.batchInsert(tableName,
                    DataSetTableOption.Family.Extension.toString(),
                    DataSetTableOption.getQualifiers(DataSetTableOption.Family.Extension),
                    new String[]{
                            lightWeightDataSet.getUrl()
                    });

        }
    }
}
