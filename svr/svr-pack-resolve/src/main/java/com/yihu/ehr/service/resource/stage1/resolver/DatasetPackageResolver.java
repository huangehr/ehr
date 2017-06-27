package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据集档案包解析器.
 *
 * @author 张进军
 * @created 2017.06.27 11:28
 */
@Component
public class DatasetPackageResolver extends PackageResolver {

    @Override
    public void resolve(StandardPackage profile, File root) throws IOException, ParseException {
        File originFolder = new File(root.getAbsolutePath());
        this.parseFiles((DatasetPackage) profile, originFolder.listFiles());
    }

    private void parseFiles(DatasetPackage profile, File[] files) throws IOException, ParseException {
        List<String> insertSqlList = new ArrayList<>();
        for (File file : files) {
            JsonNode dataNode = objectMapper.readTree(file).get("data");

            String tableName = dataNode.get("table").get("name").asText();
            String pk = dataNode.get("table").get("pk").asText();

            StringBuffer insertSqlHalf = new StringBuffer("INSERT INTO " + tableName + " ( ");
            Iterator<Map.Entry<String, JsonNode>> columnNodes = dataNode.get("columns").fields();
            while (columnNodes.hasNext()) {
                String fieldName = columnNodes.next().getValue().asText();
                insertSqlHalf.append(fieldName + ",");
            }
            insertSqlHalf.deleteCharAt(insertSqlHalf.lastIndexOf(",")).append(" ) VALUES ( ");

            // 拼接 insert sql 语句，后续批量执行保存数据。
            JsonNode rowsNode = dataNode.get("rows");
            for (int i = 0; i < rowsNode.size(); i++) {
                StringBuffer insertSql = new StringBuffer(insertSqlHalf);
                Iterator<Map.Entry<String, JsonNode>> filedNodes = rowsNode.get(i).fields();
                while (filedNodes.hasNext()) {
                    String fieldValue = filedNodes.next().getValue().asText();
                    insertSql.append(fieldValue + ",");
                }
                insertSql.deleteCharAt(insertSql.lastIndexOf(",")).append(" ); ");

                insertSqlList.add(insertSql.toString());
            }
        }
        profile.setInsertSqlList(insertSqlList);

        JsonNode headNode = objectMapper.readTree(files[0]).get("head");
        String eventDate = headNode.get("eventDate").isNull() ? "" : headNode.get("eventDate").asText();
        String createTime = headNode.get("createTime").isNull() ? "" : headNode.get("createTime").asText();
        profile.setPatientId(headNode.get("patientId").asText());
        profile.setEventNo(headNode.get("eventNo").asText());
        profile.setEventType(EventType.create(headNode.get("eventType").asText()));
        profile.setEventDate(DateTimeUtil.simpleDateParse(eventDate));
        profile.setOrgCode(headNode.get("orgCode").asText());
        profile.setCdaVersion(headNode.get("version").asText());
        profile.setCreateDate(DateTimeUtil.simpleDateParse(createTime));
    }

}
