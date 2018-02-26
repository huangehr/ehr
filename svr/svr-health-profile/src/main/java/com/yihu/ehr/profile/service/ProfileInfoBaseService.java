package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class ProfileInfoBaseService {

    @Value("${spring.application.id}")
    private String appId;
    @Autowired
    private ResourceClient resource; //资源服务
    @Autowired
    private ProfileDiseaseService profileDiseaseService;

    /**
     * @获取患者档案基本信息
     */
    public Map<String, Object> getPatientInfo(String demographicId, String version) {
        //时间排序
        Envelop envelop;
        if(version != null) {
            envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, version);
        }else {
            envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, null);
        }
        List<Map<String, Object>> list = envelop.getDetailModelList();
        if (list != null && list.size() > 0) {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                //合并数据
                Map<String, Object> result = new HashMap<>();
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!result.containsKey(key)) {
                            result.put(key, obj.get(key));
                        }
                    }
                }
                return result;
            }
        } else {
            return null;
        }
    }

    /**
     * 既往史
     * @param demographic_id
     * @return
     */
    public List<Map<String, Object>> profileHistory(String demographic_id) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //疾病史
        List<Map<String, Object>> list1 =  profileDiseaseService.getHealthProblem(demographic_id);
        if(list1 != null && list1.size() > 0) {
            Map<String, Object> medicalHistory = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder("患者于：");
            int index = 1;
            for (Map temp : list1) {
                String firstVisitDate = (String) temp.get("firstVisitDate");
                String healthProblemName = (String) temp.get("healthProblemName");
                stringBuilder.append("(" + index + ").");
                stringBuilder.append(firstVisitDate.substring(0, firstVisitDate.indexOf("T")));
                stringBuilder.append("，被诊断：");
                stringBuilder.append(healthProblemName);
                stringBuilder.append(";");
                index ++;
            }
            medicalHistory.put("label", "疾病史");
            medicalHistory.put("info", stringBuilder.toString());
            resultList.add(medicalHistory);
        }
        Envelop envelop;
        //传染病史
        String q2 = "{\"q\":\"demographic_id:" + demographic_id + "%20AND%20EHR_002393:*\"}";
        envelop = resource.getMasterData(q2, null, null, null);
        List<Map<String, Object>> list2 = envelop.getDetailModelList();
        if(list2 != null && list2.size() > 0) {
            Map<String, Object> infectiousDisease = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder("患者于：");
            int index = 1;
            for(Map temp : list2) {
                String fillDate;
                if(temp.get("EHR_002382") != null) {
                    fillDate = (String) temp.get("EHR_002382");
                }else {
                    fillDate = (String) temp.get("EHR_002393");
                }
                String name = (String) temp.get("EHR_002386");
                stringBuilder.append("(" + index + ").");
                stringBuilder.append(fillDate.substring(0, fillDate.indexOf("T")));
                stringBuilder.append("，感染：");
                stringBuilder.append(name);
                stringBuilder.append(";");
                index ++;
            }
            infectiousDisease.put("label", "传染病史");
            infectiousDisease.put("info", stringBuilder.toString());
            resultList.add(infectiousDisease);
        }
        //预防接种史
        String q3 = "{\"q\":\"demographic_id:" + demographic_id + "%20AND%20EHR_002443:*\"}";
        envelop = resource.getMasterData(q3, null, null, null);
        List<Map<String, Object>> list3 = envelop.getDetailModelList();
        if(list3 != null && list3.size() > 0) {
            Map<String, Object> vaccination = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder("患者于：");
            int index = 1;
            for (Map temp : list3) {
                String vaccinationDate = (String) temp.get("EHR_002443");
                String name = (String) temp.get("EHR_002449");
                stringBuilder.append("(" + index + ").");
                stringBuilder.append(vaccinationDate.substring(0, vaccinationDate.indexOf("T")));
                stringBuilder.append("，接种：");
                stringBuilder.append(name);
                stringBuilder.append(";");
                index ++;
            }
            vaccination.put("label", "预防接种史");
            vaccination.put("info", stringBuilder.toString());
            resultList.add(vaccination);
        }
        //手术史
        String q4 = "{\"q\":\"demographic_id:" + demographic_id + "\"}";
        envelop = resource.getMasterData(q4, null, null, null);
        List<Map<String, Object>> list4 = envelop.getDetailModelList();
        if(list4 != null && list4.size() > 0) {
            Map<String, Object> surgery = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder("患者于：");
            int index = 1;
            for (Map temp : list4) {
                String masterRowKey = (String) temp.get("rowkey");
                String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSD00_06$*" + "\"}";
                envelop = resource.getSubData(subQ, null, null, null);
                List<Map<String, Object>> subList = envelop.getDetailModelList();
                if (subList != null && subList.size() > 0) {
                    for (Map subTemp : subList) {
                        String eventDate = (String) temp.get("event_date");
                        String operateName;
                        if (temp.get("EHR_000418") != null) {
                            operateName = (String) subTemp.get("EHR_000418");
                        } else {
                            operateName = (String) subTemp.get("EHR_004045");
                        }
                        stringBuilder.append("(" + index + ").");
                        stringBuilder.append(eventDate.substring(0, eventDate.indexOf("T")));
                        stringBuilder.append("，进行手术：");
                        stringBuilder.append(operateName);
                        stringBuilder.append(";");
                        index ++;
                        break;
                    }
                }
            }
            if(index > 1) {
                surgery.put("label", "手术史");
                surgery.put("info", stringBuilder.toString());
                resultList.add(surgery);
            }
        }
        //孕产史
        String q5 = "{\"q\":\"demographic_id:" + demographic_id + "%20AND%20EHR_002443:*\"}";
        envelop = resource.getMasterData(q5, null, null, null);
        List<Map<String, Object>> list5 = envelop.getDetailModelList();
        if(list5 != null && list5.size() > 0) {
            Map<String, Object> childbirth = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder("患者于：\r\n");
            int index = 1;
            for (Map temp : list5) {
                String childbirthDate = (String) temp.get("EHR_001630");
                String end = (String) temp.get("EHR_001654");
                stringBuilder.append("(" + index + ").");
                stringBuilder.append(childbirthDate.substring(0, childbirthDate.indexOf("T")));
                stringBuilder.append("，分娩：");
                stringBuilder.append(end);
                stringBuilder.append(";");
                index ++;
            }
            childbirth.put("label", "孕产史");
            childbirth.put("info", stringBuilder.toString());
            resultList.add(childbirth);
        }
        return resultList;
    }


    private int CompareAgeOfDisease(String AgeOfDisease1,String AgeOfDisease2){
        int year1=0;
        int month1=0;
        int year2=0;
        int month2=0;
        if(AgeOfDisease1.split("年|个月").length>1) {
            year1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
            month1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[1]);
        }
        else
            month1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
        if(AgeOfDisease2.split("年|个月").length>1) {
            year2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
            month2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[1]);
        }
        else
            month2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
        if(year1 * 12 + month1 <= year2 * 12 + month2)
            return 1;
        else
            return 0;
    }

    /**
     * 全文检索
     */
    public Envelop getProfileLucene(String startTime,String endTime,List<String> lucene,Integer page,Integer size) throws Exception {
        String queryParams = "";
        if(startTime!=null && startTime.length()>0 && endTime!=null && endTime.length()>0) {
            queryParams = BasisConstant.eventDate+":["+startTime+" TO "+endTime+"]";
        }
        else {
            if(startTime!=null && startTime.length()>0) {
                queryParams = BasisConstant.eventDate+":["+startTime+" TO *]";
            }
            else if(endTime!=null && endTime.length()>0){
                queryParams = BasisConstant.eventDate+":[* TO "+endTime+"]";
            }
        }
        //全文检索
        Envelop re = resource.getResources(BasisConstant.patientEvent, "*", "*", "{\"q\":\""+queryParams.replace(' ','+')+"\"}", page, size);
        return re;
    }
}
