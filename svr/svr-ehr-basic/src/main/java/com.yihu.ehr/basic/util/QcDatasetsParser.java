package com.yihu.ehr.basic.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.model.report.json.QcDailyDatasetModel;
import com.yihu.ehr.model.report.json.QcDailyDatasetsModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
public class QcDatasetsParser {
    /**
     * 结构化档案包数据集处理
     *
     * @param root
     * @return
     */
    public static QcDailyDatasetsModel parseStructuredJsonQcDatasetsModel(JsonNode root) {
        QcDailyDatasetsModel datasetsModel = new  QcDailyDatasetsModel();

        String orgCode = root.get("org_code").asText();
        String createTime = root.get("create_date").isNull() ? "" : root.get("create_date").asText();
        String eventTime = root.path("event_time").isNull() ? "" : root.path("event_time").asText();
        String version = root.get("inner_version").asText();
        String totalNum = root.get("total_num").asText();
        String realNum = root.get("real_num").asText();
        try {
            datasetsModel.setOrgCode(orgCode);
            datasetsModel.setEventTime(eventTime);
            datasetsModel.setCreateDate(createTime);
            datasetsModel.setInnerVersion(version);
            datasetsModel.setTotalHospitalNum(Integer.valueOf(totalNum));
            datasetsModel.setRealHospitalNum(Integer.valueOf(realNum));

            List<QcDailyDatasetModel> qcDailyDatasetModels = new ArrayList<>();
            JsonNode totalDataNode = root.get("total");
            JsonNode realDataNode = root.get("real");
            for (int i = 0; i < totalDataNode.size(); ++i) {
                JsonNode recordNode = totalDataNode.get(i);
                QcDailyDatasetModel qcDailyDatasetModel = new QcDailyDatasetModel();
                qcDailyDatasetModel.setDataset(recordNode.asText());
                qcDailyDatasetModel.setAcqFlag(0);
                qcDailyDatasetModels.add(qcDailyDatasetModel);
            }
            for (int i = 0; i < realDataNode.size(); ++i) {
               JsonNode recordNode = totalDataNode.get(i);
                for(QcDailyDatasetModel qcDailyDatasetModel : qcDailyDatasetModels){
                    if(qcDailyDatasetModel.getDataset().equals(recordNode.asText())){
                        qcDailyDatasetModel.setAcqFlag(1);
                    }
                }
            }
            datasetsModel.setQcDailyDatasetModels(qcDailyDatasetModels);
        } catch (NullPointerException e) {
            throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
        }

        return datasetsModel;
    }

}
