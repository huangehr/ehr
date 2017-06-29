package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void resolve(StandardPackage profile, File root) throws IOException, ParseException {
        File originFolder = new File(root.getAbsolutePath());
        this.parseFiles((DatasetPackage) profile, originFolder.listFiles());
    }

    private void parseFiles(DatasetPackage profile, File[] files) throws IOException, ParseException {
        List<String> sqlList = new ArrayList<>();

        for (File file : files) {
            // head 节点
            JsonNode headNode = objectMapper.readTree(file).get("head");
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

            // 拼接 insert/update 语句，后续批量执行保存数据。
            for (int i = 0, length = rowsNode.size(); i < length; i++) {
                JsonNode rowNode = rowsNode.get(i);

                // 判断是 insert，还是 update。
                StringBuffer hasRecordSql = new StringBuffer(" SELECT 1 FROM " + tableName + " WHERE ");
                for (String pk : pkArr) {
                    hasRecordSql.append(pk + " = '" + rowNode.get(pk).asText() + "' AND ");
                }
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
                    sql.append(fieldName + " = '" + fieldValue + "', ");
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

        // 因本地调试而暂时注释。
        // Todo 部分字段待确定。
        JsonNode headNode = objectMapper.readTree(files[0]).get("head");
//        String eventDate = headNode.get("eventDate").isNull() ? "" : headNode.get("eventDate").asText();
        String createTime = headNode.get("createTime").isNull() ? "" : headNode.get("createTime").asText();
//        profile.setPatientId(headNode.get("patientId").asText());
//        profile.setEventNo(headNode.get("eventNo").asText());
//        profile.setEventType(EventType.create(headNode.get("eventType").asText()));
//        profile.setEventDate(DateTimeUtil.simpleDateParse(eventDate));
//        profile.setOrgCode(headNode.get("orgCode").asText());
//        profile.setCdaVersion(headNode.get("version").asText());
//        profile.setCreateDate(DateTimeUtil.simpleDateParse(createTime));
    }

}
