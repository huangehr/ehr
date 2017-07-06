package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.config.EventIndexConfig;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.exception.LegacyPackageException;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import com.yihu.ehr.service.resource.stage1.PackModelFactory;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage1.extractor.KeyDataExtractor;
import com.yihu.ehr.util.PackResolveLog;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * 数据集（非档案类型）档案包解析器.
 *
 * @author 张进军
 * @created 2017.06.27 11:28
 */
@Component
public class DatasetPackageResolver extends PackageResolver {

    Logger logger = LoggerFactory.getLogger(DatasetPackageResolver.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventIndexConfig eventIndex;

    @Override
    public List<StandardPackage> resolveDataSets( File root,String clinetId) throws Exception {
        File originFolder = new File(root.getAbsolutePath());
        return this.parseDataSetFiles(clinetId, originFolder.listFiles(),false);
    }

    @Override
    public void resolve(StandardPackage profile, File root) throws Exception {
        File originFolder = new File(root.getAbsolutePath());
        this.parseFiles((DatasetPackage) profile, originFolder.listFiles());
    }

    /**
     * 非档案类型--结构化档案包解析JSON文件中的数据。
     */
    private List<StandardPackage> parseDataSetFiles(String clinetId, File[] files, boolean origin) throws Exception {
        List<StandardPackage> profiles = new ArrayList<>();
        StandardPackage profile = null;
        for (File file : files) {
            List<PackageDataSet> packageDataSets = generateDataSet(file, origin);
            for (PackageDataSet dataSet:packageDataSets) {
                profile = PackModelFactory.createPackModel(ProfileType.Dataset);
                String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
                dataSet.setCode(dataSetCode);

                // Extract key data from data set if exists
                if (!origin) {
                    //就诊卡信息
                    if (StringUtils.isEmpty(profile.getCardId())) {
                        Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                        String cardId = (String) properties.get(MasterResourceFamily.BasicColumns.CardId);
                        if (!StringUtils.isEmpty(cardId)) {
                            profile.setCardId(cardId);
                            profile.setCardType((String) properties.get(MasterResourceFamily.BasicColumns.CardType));
                        }
                    }

                    //身份信息
                    if (StringUtils.isEmpty(profile.getDemographicId()) || StringUtils.isEmpty(profile.getPatientName())) {
                        Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.DemographicInfo);

                        String demographicId = (String) properties.get(MasterResourceFamily.BasicColumns.DemographicId);
                        if (!StringUtils.isEmpty(demographicId) && StringUtils.isEmpty(profile.getDemographicId())) {
                            profile.setDemographicId(demographicId);
                        }

                        String patientName = (String) properties.get(MasterResourceFamily.BasicColumns.PatientName);
                        if (!StringUtils.isEmpty(patientName) && StringUtils.isEmpty(profile.getPatientName())) {
                            profile.setPatientName(patientName);
                        }
                    }

                    //就诊事件信息
                    if (profile.getEventDate() == null) {
                        Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                        Date eventDate = (Date) properties.get(MasterResourceFamily.BasicColumns.EventDate);
                        if (eventDate != null) {
                            profile.setEventDate(eventDate);
                            profile.setEventType((EventType) properties.get(MasterResourceFamily.BasicColumns.EventType));
                        }
                    }

                    //门诊/住院诊断
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                    List<String> diagnosisList = (List<String>) properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                    if (diagnosisList != null && diagnosisList.size() > 0) {
                        profile.setDiagnosisList(diagnosisList);
                    }
                }

                profile.setPatientId(dataSet.getPatientId());
                profile.setEventNo(dataSet.getEventNo());
                profile.setOrgCode(dataSet.getOrgCode());
                profile.setCdaVersion(dataSet.getCdaVersion());
                profile.setCreateDate(dataSet.getCreateTime());
                profile.insertDataSet(dataSetCode, dataSet);
                profile.setClientId(clinetId);
                profile.regularNonArchiveRowKey();//rowkey设置
                profiles.add(profile);
            }
        }

        return profiles;
    }


    /**
     * 生产数据集
     *
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    private List<PackageDataSet> generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        List<PackageDataSet> dataSets = parseNonArchiveJsonDataSet(jsonNode);
        return dataSets;
    }

    /**
     * 解析 .json 文件中的 JSON 数据，拼接成SQL语句
     * @param profile
     * @param files
     * @throws IOException
     * @throws ParseException
     */
    private void parseFiles(DatasetPackage profile, File[] files) throws IOException, ParseException {
        List<String> sqlList = new ArrayList<>();

        for (File file : files) {
            // head 节点
            JsonNode headNode = objectMapper.readTree(file).get("head");
            String transactionId = headNode.get("id").asText();
            String version = headNode.get("version").asText();
            String sourceTable = headNode.get("source").asText();
            String targetTable = headNode.get("target").asText();
            // data 节点
            JsonNode dataNode = objectMapper.readTree(file).get("data");
            String tableName = dataNode.get("table").get("name").asText();
            String[] pkArr = dataNode.get("table").get("pk").asText().split(",");
            // columns 节点
            JsonNode columnsNode = dataNode.get("columns");
            // rows 节点
            JsonNode rowsNode = dataNode.get("rows");

            // 判断标准版本是否存在。
            String isExistVersionSql = "SELECT 1 FROM std_cda_versions WHERE version = '" + version + "'";
            if (jdbcTemplate.queryForList(isExistVersionSql).size() == 0) {
                throw new RuntimeException("标准版本号不存在，version: " + version);
            }
            // 判断表是否存在。
            String isExistTableSql = "SELECT 1 FROM std_data_set_" + version + " WHERE code = '" + tableName + "'";
            if (jdbcTemplate.queryForList(isExistTableSql).size() == 0) {
                throw new RuntimeException("标准中不存在该表，version: " + version + ", table: " + tableName);
            }

            // 拼接 insert/update 语句，后续批量执行保存数据。
            for (int i = 0, length = rowsNode.size(); i < length; i++) {
                JsonNode rowNode = rowsNode.get(i);

                // 用于记录日志：日志JSON结构中的data子节点。
                ObjectNode logDataNode = objectMapper.createObjectNode();
                ObjectNode logDataTargetIdNode = objectMapper.createObjectNode();
                logDataNode.put("transactionId", transactionId);
                logDataNode.put("target", targetTable);
                logDataNode.set("source_id", rowNode.get("_id"));

                // 判断是 insert，还是 update。
                StringBuffer hasRecordSql = new StringBuffer(" SELECT 1 FROM " + tableName + " WHERE ");
                for (String pk : pkArr) {
                    String pkValue = rowNode.get(pk).asText();
                    hasRecordSql.append(pk + " = '" + pkValue + "' AND ");
                    logDataTargetIdNode.put(pk, pkValue);
                }

                logDataNode.set("target_id", logDataTargetIdNode);
                PackResolveLog.info("", logDataNode);

                int hasRecordSqlLen = hasRecordSql.length();
                hasRecordSql.delete(hasRecordSqlLen - 4, hasRecordSqlLen);
                boolean isInsert = jdbcTemplate.queryForList(hasRecordSql.toString()).size() == 0 ? true : false;

                StringBuffer sql = new StringBuffer();
                if (isInsert) {
                    sql.append(" INSERT INTO " + tableName + " SET ");
                } else {
                    sql.append(" UPDATE " + tableName + " SET ");
                }
                for (JsonNode column : columnsNode) {
                    String fieldName = column.get("column").asText();
                    String fieldValue = rowNode.get(fieldName).asText();

                    // 判断表字段是否存在。
                    String fieldSql = "SELECT f.column_type AS column_type FROM std_meta_data_" + version + " f  " +
                            "LEFT JOIN std_data_set_" + version + " t ON t.id = f.dataset_id" +
                            "WHERE t.code = '" + tableName + "' AND f.column_name = '" + fieldName + "'";
                    if (jdbcTemplate.queryForList(fieldSql).size() == 0) {
                        throw new RuntimeException("标准中不存在该表字段，version: " + version + ", table: " + tableName + ", field: " + fieldName);
                    }

                    // 判断字段类型
                    String columnType = jdbcTemplate.queryForMap(fieldSql).get("column_type").toString().toUpperCase();
                    if (columnType.contains("VARCHAR")) {
                        sql.append(fieldName + " = '" + fieldValue + "', ");
                    } else if (columnType.contains("TINYINT") || columnType.contains("NUMBER")) {
                        sql.append(fieldName + " = " + fieldValue + ", ");
                    } else if (columnType.contains("DATE")) {
                        sql.append(fieldName + " = '" + DateTimeUtil.simpleDateFormat(DateTimeUtil.simpleDateParse(fieldValue)) + "', ");
                    } else if (columnType.contains("DATETIME")) {
                        sql.append(fieldName + " = '" + DateTimeUtil.simpleDateTimeFormat(DateTimeUtil.simpleDateParse(fieldValue)) + "', ");
                    }
                }
                sql.deleteCharAt(sql.lastIndexOf(","));
                if (!isInsert) {
                    sql.append(" WHERE ");
                    for (String pk : pkArr) {
                        sql.append(pk + " = '" + rowNode.get(pk).asText() + "' AND ");
                    }
                    int sqlLen = sql.length();
                    sql.delete(sqlLen - 4, sqlLen);
                }
                sql.append(";");

                sqlList.add(sql.toString());
            }
        }
        profile.setSqlList(sqlList);
    }

    /**
     *  TODO 未完整逻辑,返回列表
     *  add by HZY at 2017/07/03
     * （非档案类型）结构化档案包数据集处理
     *
     * @param root
     * @return
     */
    public List<PackageDataSet> parseNonArchiveJsonDataSet(JsonNode root) {
        List<PackageDataSet> packageDataSetList = new ArrayList<>();
        PackageDataSet dataSet = null;
        JsonNode head = root.get("head");//文件内容头信息
        JsonNode data = root.get("data");//文件内容主体信息

        String version = head.get("version").asText();
        if (version.equals("000000000000")) throw new LegacyPackageException("Package is collected via cda version 00000000000, ignored.");

        String dataSetCode = head.get("target").asText();
        String createTime = head.get("createTime").isNull() ? "" : head.get("createTime").asText();
        String orgCode = head.get("orgCode").asText();

        final String[] eventNo = {""};
        final String[] patientId = {""};
        final String[] eventTime = {""};    // 旧数据集结构可能不存在这个属性
        JsonNode table =  data.get("table");//表
        List<String> pkList = Arrays.asList(table.path("pk").asText().split(","));//主键字段
        ArrayNode columns = (ArrayNode) data.get("columns");//列名
        ArrayNode rows = (ArrayNode) data.get("rows");//列值

        //获取索引字段
        columns.forEach(item -> {
            //事件号字段获取
            if (eventIndex.getEventNo().contains(item.get("column").asText())){
                 eventNo[0] = item.get("column").asText();
            }
            //病人ID字段获取
            if (eventIndex.getPatientId().contains(item.get("column").asText())){
                patientId[0] = item.get("column").asText();
            }
            //时间时间获取
            if (eventIndex.getEventTime().contains(item.get("column").asText())){
                eventTime[0] = item.path("column").isNull() ? "" : item.path("column").asText();    // 旧数据集结构可能不存在这个属性
            }
        });

        StringBuffer pkBuffer = new StringBuffer();
        for (int i = 0; i < rows.size(); ++i) {
            JsonNode recordNode = rows.get(i);
            try {
                dataSet  = new PackageDataSet();
                dataSet.setPatientId(recordNode.path(patientId[0]).asText());
                dataSet.setEventNo(recordNode.path(eventNo[0]).asText());
                dataSet.setCdaVersion(version);
                dataSet.setCode(dataSetCode);
                dataSet.setOrgCode(orgCode);
                dataSet.setEventTime(DateTimeUtil.simpleDateParse(recordNode.path(eventTime[0]).asText()));
                dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));
                MetaDataRecord record = new MetaDataRecord();

                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaData = item.getKey();
                    if (metaData.equals("EVENT_NO")) continue; //metaData.equals("PATIENT_ID") ||
                    if (metaData.equals("_id")) continue;//源表主键字段名
                    String value = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                    record.putMetaData(metaData, value);
                    if (pkList!=null && pkList.contains(metaData)){
                        pkBuffer.append(value).append("_");
                    }
                }

                dataSet.setPk(pkBuffer.toString());
                dataSet.addRecord(Integer.toString(i), record);
                packageDataSetList.add(dataSet);
            } catch (NullPointerException e) {
                throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
            } catch (ParseException e) {
                throw new RuntimeException("Invalid date time format.");
            }
        }

        return packageDataSetList;
    }

}
