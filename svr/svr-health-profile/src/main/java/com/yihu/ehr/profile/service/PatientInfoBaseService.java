package com.yihu.ehr.profile.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoBaseService {

    @Value("${spring.application.id}")
    private String appId;

    static class EventDateComparatorDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String eventDate1 = (String)m1.get(BasisConstant.eventDate);
            String eventDate2 = (String)m2.get(BasisConstant.eventDate);
            String str1 = eventDate1.substring(0, eventDate1.length()-1).replaceAll("[a-zA-Z]"," ");
            String str2 = eventDate2.substring(0, eventDate1.length()-1).replaceAll("[a-zA-Z]"," ");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date v1 = null;
            Date v2 = null;
            try {
                v1 = dateFormat.parse(str1);
                v2 = dateFormat.parse(str2);
            }catch (Exception e) {
                e.printStackTrace();
            }
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }
    }
    @Autowired
    private XResourceClient resource; //资源服务
    @Autowired
    private XRedisServiceClient redisServiceClient;

    /**
     * 根据时间获取病龄
     * @param eventData
     * @return
     */
    private String getAgeOfDisease(Object eventData){
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
        String eventDataYear=eventData.toString().substring(0, 7).substring(0,4);
        String eventDataMonth=eventData.toString().substring(0, 7).substring(5,7);
        String ageOfDisease = "";
        if(Integer.parseInt(sd.format(new Date()).substring(4, 6)) - Integer.parseInt(eventDataMonth)<0){
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear)-1;
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))+12- Integer.parseInt(eventDataMonth);
            if(year>0) {
                ageOfDisease = year + "年" + month + "个月";
            }
            else{
                ageOfDisease = month + "个月";
            }
        }
        else{
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear);
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))- Integer.parseInt(eventDataMonth);
            if(year>0) {
                ageOfDisease = year + "年" + month + "个月";
            }
            else{
                ageOfDisease = month + "个月";
            }
        }
        return ageOfDisease;
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
     * @获取患者档案基本信息  (考虑改造、mysql取值、保密字段!!!)
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

    /*
     * @根据患者住院门诊记录做健康问题统计
     */
    public List<Map<String, Object>> getHealthProblem(String demographicId) {
        List<Map<String, Object>> result = new ArrayList<>();
        //获取门诊住院记录
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, null);
        Map<String, List<Map<String, Object>>> hpMap = new HashedMap();
        if (envelop.getDetailModelList() != null && envelop.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = envelop.getDetailModelList();
            //进行降序
            Collections.sort(eventList, new EventDateComparatorDesc());
            for(Map<String, Object> event : eventList) {
                 if(event.containsKey(BasisConstant.healthProblem)) {
                     String healthProblem = event.get(BasisConstant.healthProblem).toString();
                     if(!StringUtils.isEmpty(healthProblem)) {
                         String[] hps = healthProblem.split(";");
                         for(String hp : hps) {
                             List<Map<String, Object>> profileList = new ArrayList<>();
                             if(hpMap.containsKey(hp)) {
                                 profileList = hpMap.get(hp);
                             }
                             profileList.add(event);
                             hpMap.put(hp, profileList);
                         }
                     }
                 }
            }
            for(String healthProblemCode : hpMap.keySet()) {
                Map<String, Object> obj = new HashedMap();
                obj.put("healthProblemCode", healthProblemCode);
                obj.put("healthProblemName", redisServiceClient.getHealthProblemRedis(healthProblemCode));
                int visitTimes = 0;
                int hospitalizationTimes = 0;
                List<Map<String,Object>> profileList = hpMap.get(healthProblemCode);
                for(int i = 0; i < profileList.size() ; i++) {
                    Map<String, Object> profile = profileList.get(i);
                    //事件类型
                    String eventType = (String) profile.get(BasisConstant.eventType);
                    String recentEvent = "";
                    if("0".equals(eventType)) {
                        recentEvent =  "门诊";
                        visitTimes++;
                    }
                    else if("1".equals(eventType)) {
                        recentEvent =  "住院";
                        hospitalizationTimes++;
                    }
                    else if("2".equals(eventType)) {
                        recentEvent =  "体检";
                    }
                    //第一条
                    if(i == 0) {
                        obj.put("lastVisitDate", profile.get(BasisConstant.eventDate));
                        obj.put("lastVisitOrgCode", profile.get(BasisConstant.orgCode));
                        obj.put("lastVisitOrg", profile.get(BasisConstant.orgName));
                        obj.put("lastVisitRecord", profile.get(BasisConstant.rowkey));
                        obj.put("recentEvent", recentEvent);
                        obj.put("eventType", eventType);
                    }
                    //最后一条
                    if(i==profileList.size()-1) {
                        obj.put("ageOfDisease",getAgeOfDisease(profile.get(BasisConstant.eventDate)));
                    }
                }
                obj.put("visitTimes", visitTimes);
                obj.put("hospitalizationTimes", hospitalizationTimes);
                result.add(obj);
            }
        }
        return result;
    }
		
    /*
     * @根据患者最后一次诊断记录获取诊断详情
     */
    public List<Map<String, Object>> getHealthProblemSub(String lastVisitRecord, String eventType) {
        List<Map<String, Object>> result = new ArrayList<>();
        Envelop envelop = resource.getSubData("{\"q\":\"profile_id:" + lastVisitRecord + "\"}", null, null, null);
        List<Map<String, Object>> envelopList = envelop.getDetailModelList();
        Map<String, Object> onlyMap = new HashMap<>();
        for(Map<String, Object> temp : envelopList) {
           for(String key : temp.keySet()) {
               if(!onlyMap.containsKey(key)) {
                    onlyMap.put(key, temp.get(key));
               }
           }
        }
        Map<String, Object> newKeyObject = new HashMap<String, Object>();
        if(eventType.equals("0")) {
            newKeyObject.put("DiagnosticTypeCode", "门诊");
            newKeyObject.put("DiagnosticDate", onlyMap.get("EHR_000113") != null ? onlyMap.get("EHR_000113") : "");
            newKeyObject.put("SignatureDoctor", onlyMap.get("EHR_000106") != null ? onlyMap.get("EHR_000106") : "");
            newKeyObject.put("DiagnosticName", onlyMap.get("EHR_000112") != null ? onlyMap.get("EHR_000112") : "");
            newKeyObject.put("DiagnosticInstructions", onlyMap.get("EHR_000114") != null ? onlyMap.get("EHR_000114") : "");
        }else if(eventType.equals("1")) {
            newKeyObject.put("DiagnosticTypeCode", "住院");
            newKeyObject.put("DiagnosticDate", onlyMap.get("EHR_000296") != null ? onlyMap.get("EHR_000296") : "");
            newKeyObject.put("SignatureDoctor", onlyMap.get("EHR_000290") != null ? onlyMap.get("EHR_000290") : "");
            newKeyObject.put("DiagnosticName", onlyMap.get("EHR_000295") != null ? onlyMap.get("EHR_000295") : "");
            newKeyObject.put("DiagnosticInstructions", onlyMap.get("EHR_000297") != null ? onlyMap.get("EHR_000297") : "");
        }else if(eventType.equals("2")) {
            newKeyObject.put("DiagnosticTypeCode", "体检");
        }else {
            newKeyObject.put("DiagnosticTypeCode", eventType);
        }
        result.add(newKeyObject);
        return result;
    }
		
    /**
     * 全文检索
     */
    public Envelop getProfileLucene(String startTime,String endTime,List<String> lucene,Integer page,Integer size) throws Exception
    {
        String queryParams = "";
        if(startTime!=null && startTime.length()>0 && endTime!=null && endTime.length()>0)
        {
            queryParams = BasisConstant.eventDate+":["+startTime+" TO "+endTime+"]";
        }
        else {
            if(startTime!=null && startTime.length()>0)
            {
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
