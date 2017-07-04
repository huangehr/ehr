package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.util.PackResolveLog;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
    public void resolve(StandardPackage profile, File root) throws IOException, ParseException {
        File originFolder = new File(root.getAbsolutePath());
        this.parseFiles((DatasetPackage) profile, originFolder.listFiles());
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

}
