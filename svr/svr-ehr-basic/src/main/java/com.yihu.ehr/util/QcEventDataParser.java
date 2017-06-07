package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.model.report.json.QcDailyEventDetailModel;
import com.yihu.ehr.model.report.json.QcDailyDatasetsModel;
import com.yihu.ehr.model.report.json.QcDailyEventDetailModel;
import com.yihu.ehr.model.report.json.QcDailyEventsModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Janseny
 * @version 1.0
 * @created 2017.05.10
 */
@Component
public class QcEventDataParser {
    /**
     * 结构化档案包数据集处理
     *
     * @param root
     * @return
     */
    public static QcDailyEventsModel parseStructuredJsonQcDatasetsModel(JsonNode root) {
        QcDailyEventsModel eventsModel = new  QcDailyEventsModel();

        String orgCode = root.get("org_code").asText();
        String createTime = root.get("create_date").isNull() ? "" : root.get("create_date").asText();
        String version = root.get("inner_version").asText();
        String patTotalNum = root.get("total_outpatient_num").asText();
        String patRealNum = root.get("real_outpatient_num").asText();
        String hosTotalNum = root.get("total_hospital_num").asText();
        String hosRealNum = root.get("real_hospital_num").asText();
        try {
            eventsModel.setOrg_code(orgCode);
            eventsModel.setCreate_date(createTime);
            eventsModel.setInner_version(version);
            eventsModel.setTotal_outpatient_num(Integer.valueOf(patTotalNum));
            eventsModel.setReal_outpatient_num(Integer.valueOf(patRealNum));
            eventsModel.setTotal_hospital_num(Integer.valueOf(hosTotalNum));
            eventsModel.setReal_hospital_num(Integer.valueOf(hosRealNum));

            List<QcDailyEventDetailModel> total_outpatient_list = new ArrayList<>();
            List<QcDailyEventDetailModel> real_outpatient_list = new ArrayList<>();
            List<QcDailyEventDetailModel> total_hospital_list = new ArrayList<>();
            List<QcDailyEventDetailModel> real_hospital_list = new ArrayList<>();

            JsonNode totalPatDataNode = root.get("total_outpatient");
            JsonNode realPatDataNode = root.get("real_outpatient");
            JsonNode totalHosDataNode = root.get("total_hospital");
            JsonNode realHosDataNode = root.get("real_hospital");

            for (int i = 0; i < totalPatDataNode.size(); ++i) {
                QcDailyEventDetailModel eventDetailModel = new QcDailyEventDetailModel();
                JsonNode recordNode = totalPatDataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key =  item.getKey();
                    if(key.equals("patient_id")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setPatient_id(val);
                    }else if(key.equals("event_no")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_no(val);
                    }else if(key.equals("event_time")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_time(val);
                    }
                }
                total_outpatient_list.add(eventDetailModel);
            }

            for (int i = 0; i < realPatDataNode.size(); ++i) {
                QcDailyEventDetailModel eventDetailModel = new QcDailyEventDetailModel();
                JsonNode recordNode = totalPatDataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key =  item.getKey();
                    if(key.equals("patient_id")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setPatient_id(val);
                    }else if(key.equals("event_no")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_no(val);
                    }else if(key.equals("event_time")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_time(val);
                    }
                }
                real_outpatient_list.add(eventDetailModel);
            }

            for (int i = 0; i < totalHosDataNode.size(); ++i) {
                QcDailyEventDetailModel eventDetailModel = new QcDailyEventDetailModel();
                JsonNode recordNode = totalPatDataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key =  item.getKey();
                    if(key.equals("patient_id")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setPatient_id(val);
                    }else if(key.equals("event_no")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_no(val);
                    }else if(key.equals("event_time")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_time(val);
                    }
                }
                total_hospital_list.add(eventDetailModel);
            }

            for (int i = 0; i < realHosDataNode.size(); ++i) {
                QcDailyEventDetailModel eventDetailModel = new QcDailyEventDetailModel();
                JsonNode recordNode = totalPatDataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key =  item.getKey();
                    if(key.equals("patient_id")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setPatient_id(val);
                    }else if(key.equals("event_no")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_no(val);
                    }else if(key.equals("event_time")){
                        String val = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                        eventDetailModel.setEvent_time(val);
                    }
                }
                real_hospital_list.add(eventDetailModel);
            }

            eventsModel.setTotal_outpatient(total_outpatient_list);
            eventsModel.setReal_outpatient(real_outpatient_list);
            eventsModel.setTotal_hospital(total_hospital_list);
            eventsModel.setReal_hospital(real_hospital_list);
        } catch (NullPointerException e) {
            throw new RuntimeException("Null pointer occurs while generate data event, package cda version: " + version);
        }

        return eventsModel;
    }

}
