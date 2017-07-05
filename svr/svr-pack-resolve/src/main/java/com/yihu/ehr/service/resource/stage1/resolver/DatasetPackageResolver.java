package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.util.PackResolveLog;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yihu.ehr.service.resource.stage1.extractor.KeyDataExtractor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.yihu.ehr.util.datetime.DateTimeUtil.simpleDateTimeParse;

/**
 * 数据集档案包解析器.
 *
 * @author 张进军
 * @created 2017.06.27 11:28
 */
@Component
public class DatasetPackageResolver extends PackageResolver {

    Logger logger = LoggerFactory.getLogger(DatasetPackageResolver.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void resolveDataSet(StandardPackage profile, File root) throws Exception {
        File folder = new File(root.getAbsolutePath());
        this.parseDataSetFiles((DatasetPackage) profile, folder.listFiles());
    }

    @Override
    public void resolve(StandardPackage profile, File root) throws Exception {
        File originFolder = new File(root.getAbsolutePath());
        this.parseFiles((DatasetPackage) profile, originFolder.listFiles(),false);
    }

    /**
     * 非档案类型--结构化档案包解析JSON文件中的数据。
     */
    private void parseFiles(DatasetPackage profile, File[] files, boolean origin) throws Exception {
        for (File file : files) {
            PackageDataSet dataSet = generateDataSet(file, origin);

            String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            dataSet.setCode(dataSetCode);

            // Extract key data from data set if exists
            if (!origin) {
                //就诊卡信息
                if (StringUtils.isEmpty(profile.getCardId())) {
                    Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    String cardId = (String) properties.get(MasterResourceFamily.BasicColumns.CardId);
                    if(!StringUtils.isEmpty(cardId))
                    {
                        profile.setCardId(cardId);
                        profile.setCardType((String) properties.get(MasterResourceFamily.BasicColumns.CardType));
                    }
                }

                //身份信息
                if (StringUtils.isEmpty(profile.getDemographicId()) || StringUtils.isEmpty(profile.getPatientName())) {
                    Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.DemographicInfo);

                    String demographicId = (String) properties.get(MasterResourceFamily.BasicColumns.DemographicId);
                    if(!StringUtils.isEmpty(demographicId) &&StringUtils.isEmpty(profile.getDemographicId())) {
                        profile.setDemographicId(demographicId);
                    }

                    String patientName =(String) properties.get(MasterResourceFamily.BasicColumns.PatientName);
                    if(!StringUtils.isEmpty(patientName) &&StringUtils.isEmpty(profile.getPatientName())) {
                        profile.setPatientName(patientName);
                    }
                }

                //就诊事件信息
                if (profile.getEventDate() == null) {
                    Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                    Date eventDate = (Date)properties.get(MasterResourceFamily.BasicColumns.EventDate);
                    if(eventDate!=null)
                    {
                        profile.setEventDate(eventDate);
                        profile.setEventType((EventType) properties.get(MasterResourceFamily.BasicColumns.EventType));
                    }
                }

                //门诊/住院诊断
                Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                List<String> diagnosisList = (List<String>)properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                if(diagnosisList!=null && diagnosisList.size()>0) {
                    profile.setDiagnosisList(diagnosisList);
                }
            }

            profile.setPatientId(dataSet.getPatientId());
            profile.setEventNo(dataSet.getEventNo());
            profile.setOrgCode(dataSet.getOrgCode());
            profile.setCdaVersion(dataSet.getCdaVersion());
            profile.setCreateDate(dataSet.getCreateTime());
            profile.insertDataSet(dataSetCode, dataSet);
        }
    }


    /**
     * 生产数据集
     *
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    private PackageDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        PackageDataSet dataSet = dataSetResolverWithTranslator.parseNonArchiveJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }

    /**
     * 解析 .json 文件中的 JSON 数据，拼接成SQL语句
     * @param profile
     * @param files
     * @throws IOException
     * @throws ParseException
     */
    private void parseDataSetFiles(DatasetPackage profile, File[] files) throws IOException, ParseException {
        List<String> sqlList = new ArrayList<>();

        for (File file : files) {
            // head 节点
            JsonNode headNode = objectMapper.readTree(file).get("head");
            String transactionId = headNode.get("id").asText();
            String orgCode = headNode.get("orgCode").asText();
            String version = headNode.get("version").asText();
            String sourceTable = headNode.get("source").asText();
            String targetTable = headNode.get("target").asText();
            String createTime = headNode.get("createTime").asText();
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
                            "LEFT JOIN std_data_set_" + version + " t ON t.id = f.dataset_id " +
                            "WHERE t.code = '" + tableName + "' AND f.column_name = '" + fieldName + "'";
                    if (jdbcTemplate.queryForList(fieldSql).size() == 0) {
                        throw new RuntimeException("标准中不存在该表字段，version: " + version + ", table: " + tableName + ", field: " + fieldName);
                    }

                    // 判断字段类型
                    String columnType = jdbcTemplate.queryForMap(fieldSql).get("column_type").toString().toUpperCase();
                    if (columnType.contains("VARCHAR")) {
                        sql.append(fieldName + " = '" + fieldValue + "', ");
                    } else if (columnType.equals("TINYINT") || columnType.contains("NUMBER")) {
                        sql.append(fieldName + " = " + fieldValue + ", ");
                    } else if (columnType.equals("DATE")) {
                        sql.append(fieldName + " = '" + DateTimeUtil.simpleDateFormat(DateTimeUtil.simpleDateParse(fieldValue)) + "', ");
                    } else if (columnType.equals("DATETIME")) {
                        sql.append(fieldName + " = '" + DateTimeUtil.simpleDateTimeFormat(DateTimeUtil.utcDateTimeParse(fieldValue)) + "', ");
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

            profile.setOrgCode(orgCode);
            profile.setCreateDate(DateTimeUtil.utcDateTimeParse(createTime));
        }
        profile.setSqlList(sqlList);
    }

}
