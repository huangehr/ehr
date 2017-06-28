package com.yihu.ehr.profile.service;


import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoBaseService {
    @Autowired
    XResourceClient resource; //资源服务

    @Autowired
    XOrganizationClient organization; //机构信息服务

    @Autowired
    XDictClient dictClient;


    @Autowired
    XGeographyClient addressClient;

    @Value("${spring.application.id}")
    String appId;

    /**
     * 根据时间获取病龄
     * @param eventData
     * @return
     */
    private String getAgeOfDisease(Object eventData){
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
        String eventDataYear=eventData.toString().substring(0, 7).substring(0,4);
        String eventDataMonth=eventData.toString().substring(0, 7).substring(5,7);
        String ageOfDisease="";


        if(Integer.parseInt(sd.format(new Date()).substring(4, 6)) - Integer.parseInt(eventDataMonth)<0){
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear)-1;
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))+12- Integer.parseInt(eventDataMonth);
            if(year>0)
            {
                ageOfDisease = year+"年"+month+"个月";
            }
            else{
                ageOfDisease = month+"个月";
            }
        }
        else{
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear);
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))- Integer.parseInt(eventDataMonth);
            if(year>0)
            {
                ageOfDisease = year+"年"+month+"个月";
            }
            else{
                ageOfDisease = month+"个月";
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
            year1= Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
            month1= Integer.parseInt(AgeOfDisease1.split("年|个月")[1]);
        }
        else
            month1= Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
        if(AgeOfDisease2.split("年|个月").length>1) {
            year2= Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
            month2= Integer.parseInt(AgeOfDisease2.split("年|个月")[1]);
        }

        else
            month2= Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
        if(year1*12+month1<=year2*12+month2)
            return 1;
        else
            return 0;
    }


    /**
     * @获取患者档案基本信息  (考虑改造、mysql取值、保密字段!!!)
     */
    public Map<String, Object> getPatientInfo(String demographicId) throws Exception {
        //时间排序
        Envelop result = resource.getResources(BasisConstant.patientInfo, appId,null, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> list = result.getDetailModelList();
            if (list.size() == 1) {
                return list.get(0);
            } else {
                Map<String, Object> re = new HashMap<>();
                /***************** 需要合并数据 **************************/
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!re.containsKey(key)) {
                            re.put(key, obj.get(key));
                        }
                    }
                }
                /*******************************************************/

                return re;
            }
        } else {
            throw new Exception("查无此人！");
        }
    }

    /*
     * @根据患者住院门诊记录做健康问题统计 (可根据健康问题索引统计!!!)
     */
    public List<Map<String, Object>> getHealthProblem(String demographicId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        /*//获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, null, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            //健康问题+相关门诊记录
            Map<String, List<String>> outpatientMap = new HashMap<>();
            //健康问题+相关住院记录
            Map<String, List<String>> hospitalizedMap = new HashMap<>();
            StringBuilder rowkeys = new StringBuilder();

            for(Map<String,Object> event : eventList)
            {
                if(rowkeys.length() > 0)
                {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + event.get("rowkey"));
            }

            String queryParams = "{\"q\":\"" + rowkeys.toString() + "\"}";
            //门诊诊断
            Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis, appId, null, queryParams.replace(' ','+'),1,1000);///"{\"join\":\"demographic_id:" + demographicId + "\"}");
            if (outpatient.getDetailModelList() != null && outpatient.getDetailModelList().size() > 0) {


                for (int i = 0; i < outpatient.getDetailModelList().size(); i++) {
                    Map<String, Object> obj = (Map<String, Object>) outpatient.getDetailModelList().get(i);
                    if (obj.containsKey(BasisConstant.mzzd)) {
                        String code = obj.get(BasisConstant.mzzd).toString();
                        String profileId = obj.get(BasisConstant.profileId).toString();
                        //通过疾病ID获取健康问题
                        MIcd10Dict icd10Dict = ict10Dict.getIcd10DictValue(code);
                        String healthProblem = icd10Dict.getCode() + "__" + icd10Dict.getName();
                        List<String> profileList = new ArrayList<>();
                        if (outpatientMap.containsKey(healthProblem)) {
                            profileList = outpatientMap.get(healthProblem);
                            if (!profileList.contains(profileId)) {
                                profileList.add(profileId);
                            }
                        } else {
                            profileList.add(profileId);
                        }
                        outpatientMap.put(healthProblem, profileList);
                    }
                }
            }

            //住院诊断
            Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis, appId, null, queryParams.replace(' ','+'),1,1000); //"{\"join\":\"demographic_id:" + demographicId + "\"}");
            if (hospitalized.getDetailModelList() != null && hospitalized.getDetailModelList().size() > 0) {
                for (int i = 0; i < hospitalized.getDetailModelList().size(); i++) {
                    Map<String, Object> obj = (Map<String, Object>) hospitalized.getDetailModelList().get(i);
                    if (obj.containsKey(BasisConstant.zyzd)) {
                        String code = obj.get(BasisConstant.zyzd).toString();
                        String profileId = obj.get(BasisConstant.profileId).toString();
                        //通过疾病ID获取健康问题
                        MIcd10Dict icd10Dict = ict10Dict.getIcd10DictValue(code);
                        String healthProblem = icd10Dict.getCode() + "__" + icd10Dict.getName();
                        List<String> profileList = new ArrayList<>();
                        if (hospitalizedMap.containsKey(healthProblem)) {
                            profileList = hospitalizedMap.get(healthProblem);
                            if (!profileList.contains(profileId)) {
                                profileList.add(profileId);
                            }
                        } else {
                            profileList.add(profileId);
                        }
                        hospitalizedMap.put(healthProblem, profileList);
                    }
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            List<String> orgCodeList = new ArrayList<>();

            //遍历所有健康问题，门诊记录和住院记录整合
            for (String key : outpatientMap.keySet()) //门诊
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];
                List<String> profileList = outpatientMap.get(key);
                //最近的事件
                boolean breaked = false;
                Map<String, Object> obj = new HashMap<>();
                obj.put("healthProblemCode", healthProblemCode);
                obj.put("healthProblemName", healthProblemName);
                obj.put("visitTimes", profileList.size());
                obj.put("hospitalizationTimes", 0);
                obj.put("recentEvent", "就诊");
                for (String profileId : profileList) {
                    for (Map<String, Object> event : eventList) {
                        String rowkey = event.get("rowkey").toString();
                        Object eventData = event.get("event_date");
                        String orgCode = event.get("org_code").toString();
                        if(!orgCodeList.contains(orgCode))
                        {
                            orgCodeList.add(orgCode);
                        }

                        if (profileId.equals(rowkey)) {
                            if (!obj.containsKey("lastVisitDate")||sdf.parse(eventData.toString()).getTime() > sdf.parse(obj.get("lastVisitDate").toString()).getTime()) {
                                obj.put("lastVisitDate", eventData);
                                obj.put("lastVisitOrgCode", orgCode);
                                obj.put("lastVisitOrg", "");
                                obj.put("lastVisitRecord", profileId);
                                obj.put("ageOfDisease", getAgeOfDisease(eventData));
                            }
                            break;
                        }
                    }
                }
                re.add(obj);
            }

            for (String key : hospitalizedMap.keySet()) //住院
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];

                List<String> profileList = hospitalizedMap.get(key);
                //最近的事件
                boolean breaked = false;
                for (Map<String, Object> event : eventList) {
                    String rowkey = event.get("rowkey").toString();
                    Object eventData = event.get("event_date");
                    String orgCode = event.get("org_code").toString();
                    if(!orgCodeList.contains(orgCode))
                    {
                        orgCodeList.add(orgCode);
                    }

                    for (String profileId : profileList) {
                        if (profileId.equals(rowkey)) {
                            //判断是否已经存在
                            for (Map<String, Object> obj : re) {
                                if (obj.get("healthProblemCode").toString().equals(healthProblemCode)) {
                                    obj.put("hospitalizationTimes", profileList.size());
                                    //事件是否更早
                                    if (sdf.parse(eventData.toString()).getTime() > sdf.parse(obj.get("lastVisitDate").toString()).getTime()) {
                                        obj.put("lastVisitDate", eventData);
                                        obj.put("lastVisitOrgCode", orgCode);
                                        obj.put("lastVisitOrg", "");
                                        obj.put("lastVisitRecord", profileId);
                                        obj.put("recentEvent", "住院");
                                    }
                                    else if(CompareAgeOfDisease(getAgeOfDisease(eventData),obj.get("ageOfDisease").toString())==0){
                                        obj.put("ageOfDisease",getAgeOfDisease(eventData));
                                    }
                                    breaked=true;
                                    break;
                                }

                            }

                            //已存在
                            if (breaked) {
                                break;
                            } else //不存在则新增
                            {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("healthProblemCode", healthProblemCode);
                                obj.put("healthProblemName", healthProblemName);
                                obj.put("visitTimes", 0);
                                obj.put("hospitalizationTimes", profileList.size());
                                obj.put("lastVisitDate", eventData);
                                obj.put("lastVisitOrgCode", orgCode);
                                obj.put("lastVisitOrg", "");
                                obj.put("lastVisitRecord", profileId);
                                obj.put("recentEvent", "住院");
                                obj.put("ageOfDisease",getAgeOfDisease(eventData));
                                re.add(obj);
                                breaked = true;
                                break;
                            }
                        }
                    }
                }
            }

            //获取相关机构名称
            List<MOrganization> orgList = organization.getOrgs(orgCodeList);
            if(orgList!=null)
            {
                for(Map<String,Object> item:re)
                {
                    String orgCode = item.get("lastVisitOrgCode").toString();
                    //遍历医院列表获取医院名称
                    for(MOrganization org:orgList)
                    {
                        if(org.getOrgCode().equals(orgCode))
                        {
                            item.put("lastVisitOrg",org.getFullName());
                            break;
                        }

                    }
                }
            }
        }*/



        return re;
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
        Envelop re = resource.getResources(BasisConstant.patientEvent, appId, null, "{\"q\":\""+queryParams.replace(' ','+')+"\"}", page, size);
        return re;
    }
}
