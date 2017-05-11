package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.agModel.report.QcDailyMetadataModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 简易数据集解析器。仅将JSON结构中的数据集解析成KEY-VALUE模式，不提供字段翻译功能，
 * 例如将类型为S1的数据元添加_S后缀。
 *
 * 此类用于接口接受到使用数据集提供数据的情况。若是档案包中的数据集解析并存入HBase，请使用其派生类。
 *
 * @author Janseny
 * @version 1.0
 * @created 2017.05.10
 */
@Component
public class QcMetadataParser {
    /**
     * 结构化档案包数据集处理
     *
     * @param root
     * @return
     */
    public static List<QcDailyMetadataModel> parseStructuredJsonMetadateModel(JsonNode root) {

        List<QcDailyMetadataModel> metadataModels = new ArrayList<>();

        String orgCode = root.get("org_code").asText();
        String createTime = root.get("create_date").isNull() ? "" : root.get("create_date").asText();
        String eventTime = root.path("event_time").isNull() ? "" : root.path("event_time").asText();
        String version = root.get("inner_version").asText();
        String dataset = root.get("dataset").asText();
        try {
            JsonNode data = root.get("data");
            for (int i = 0; i < data.size(); ++i) {
                QcDailyMetadataModel metadataModel = new QcDailyMetadataModel();
                metadataModel.setOrg_code(orgCode);
                metadataModel.setEvent_time(eventTime);
                metadataModel.setCreate_date(createTime);
                metadataModel.setInner_version(version);
                metadataModel.setDataset(dataset);
                JsonNode recordNode = data.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key =  item.getKey();
                    if(key.equals("total")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        metadataModel.setTotalQty(Integer.valueOf(val));
                    }else if(key.equals("error")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        metadataModel.setErrorQty(Integer.valueOf(val));
                    }else{
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        metadataModel.setMetadate(key);
                        metadataModel.setErrCode(val);
                    }
                }
                metadataModels.add(metadataModel);
            }

        } catch (NullPointerException e) {
            throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
        }
        return metadataModels;
    }


}
