package com.yihu.ehr.profile.service;


import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoBaseService {
    @Autowired
    XResourceClient resource; //资源服务

    @Autowired
    XOrganizationClient organization; //机构信息服务

    //特殊字典信息服务
    @Autowired
    CD10Service dictService;

    //ICD10缓存服务
    @Autowired
    XIcd10DictClient ict10Dict;

    @Autowired
    private XHealthProblemDictClient healthProblemDictClient;

    @Autowired
    private XGeographyClient addressClient;

    String appId = "svr-health-profile";

    /**
     * @return
     * @throws Exception
     * @获取患者档案基本信息
     */
    public Map<String, Object> getPatientInfo(String demographicId) throws Exception {
        //时间排序
        Envelop result = resource.getResources(BasisConstant.patientInfo, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}");
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
     * @根据患者住院门诊记录做健康问题统计
     *"healthProblemCode":"健康问题代码",
    "healthProblemName":"高血压",
    "visitTimes":"3",
    "hospitalizationTimes":"3",
    "lastVisitDate":"2015-08-03",
    "lastVisitOrg":"厦门市第一医院",
    "vlastVisitRecord":"最新就诊档案ID",
    "medicine": "胺碘酮、奎尼丁、美西律、普罗帕酮、丙吡胺、莫雷西嗪",
    "recentEvent": "2015-08 就诊 厦门市第一医院"
     */
    public List<Map<String, Object>> getHealthProblem(String demographicId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}");
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            //健康问题+相关门诊记录
            Map<String, List<String>> outpatientMap = new HashMap<>();
            //健康问题+相关住院记录
            Map<String, List<String>> hospitalizedMap = new HashMap<>();

            //门诊诊断
            Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}");
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
            Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}");
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

            //遍历所有健康问题，门诊记录和住院记录整合
            for (String key : outpatientMap.keySet()) //门诊
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];

                List<String> profileList = outpatientMap.get(key);
                //最近的事件
                boolean breaked = false;
                for (Map<String, Object> event : eventList) {
                    String rowkey = event.get("rowkey").toString();
                    Object eventData = event.get("event_data");
                    String orgCode = event.get("orgCode").toString();
                    for (String profileId : profileList) {
                        if (profileId.equals(rowkey)) {
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("healthProblemCode", healthProblemCode);
                            obj.put("healthProblemName", healthProblemName);
                            obj.put("visitTimes", profileList.size());
                            obj.put("hospitalizationTimes", 0);
                            obj.put("lastVisitDate", eventData);
                            obj.put("lastVisitOrg", orgCode);
                            obj.put("lastVisitRecord", profileId);
                            obj.put("recentEvent", "就诊");
                            obj.put("profileId", profileId);
                            re.add(obj);
                            breaked = true;
                            break;
                        }
                        if (breaked) {
                            break;
                        }
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                    for (String profileId : profileList) {
                        if (profileId.equals(rowkey)) {
                            //判断是否已经存在
                            for (Map<String, Object> obj : re) {
                                if (obj.get("healthProblemCode").toString().equals(healthProblemCode)) {
                                    obj.put("hospitalizationTimes", profileList.size());
                                    //事件是否更早
                                    if (sdf.parse(eventData.toString()).getTime() > sdf.parse(obj.get("lastVisitDate").toString()).getTime()) {
                                        obj.put("lastVisitDate", eventData);
                                        obj.put("lastVisitOrg", orgCode);
                                        obj.put("lastVisitRecord", profileId);
                                        obj.put("recentEvent", "住院");
                                        obj.put("profileId", profileId);
                                    }
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
                                obj.put("lastVisitOrg", orgCode);
                                obj.put("lastVisitRecord", profileId);
                                obj.put("recentEvent", "住院");
                                obj.put("profileId", profileId);
                                re.add(obj);
                                breaked = true;
                                break;
                            }
                        }
                    }
                    if (breaked) {
                        break;
                    }
                }
            }

            //健康问题用药统计（另外获取）

        }

        return re;
    }

    //@（疾病模型）患者 -> 门诊住院记录
    public List<Map<String, Object>> getMedicalEvents(String demographicId, String eventType, String year, String area, String hpId, String diseaseId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //患者事件列表
        String q = "demographic_id:" + demographicId;
        String join = "";
        //事件类型
        if (eventType != null && eventType.length() > 0) {
            if (eventType.equals("1") || eventType.equals("2")) {
                q += " AND event_type:" + eventType;
            } else if (eventType.equals("3")) //根据业务来区分数据
            {
                join = " ";
            } else if (eventType.equals("4")) {
                join = " ";
            }
        }
        //事件年份
        if (year != null && year.length() > 0) {
            q += " AND event_date:[" + year + "-01-01T00:00:00Z TO " + year + "-12-31T23:59:59Z]";
        }

        String queryParams = "{\"q\":\"" + q + "\"}";

        //疾病ID
        if (diseaseId != null && diseaseId.length() > 0) {
            if (join.length() > 0) {
                join += " AND (" + BasisConstant.mzzd + ":" + diseaseId + " OR " + BasisConstant.zyzd + ":" + diseaseId + ")";
            } else {
                join = BasisConstant.mzzd + ":" + diseaseId + " OR " + BasisConstant.zyzd + ":" + diseaseId;
            }
            queryParams = "{\"q\":\"" + q + "\",\"join\":\"" + join + "\"}";
        } else {
            //健康问题
            if (hpId != null && hpId.length() > 0) {
                //健康问题->疾病ICD10代码
                List<MIcd10Dict> dictCodeList = dictService.getIcd10DictList(hpId);
                String hpJoin = "";
                if (dictCodeList != null && dictCodeList.size() > 0) {
                    String mzJoin = "";
                    String zyJoin = "";
                    //遍历疾病列表
                    for (MIcd10Dict ICD10 : dictCodeList) {
                        if (mzJoin.length() > 0) {
                            mzJoin += " OR " + BasisConstant.mzzd + ":" + ICD10.getCode();
                            zyJoin += " OR " + BasisConstant.zyzd + ":" + ICD10.getCode();
                        } else {
                            mzJoin = BasisConstant.mzzd + ":" + ICD10.getCode();
                            zyJoin = BasisConstant.zyzd + ":" + ICD10.getCode();
                        }
                    }

                    if (mzJoin.length() > 0 && zyJoin.length() > 0) {
                        hpJoin = "(" + mzJoin + ") OR (" + zyJoin + ")";
                    } else {
                        if (mzJoin.length() > 0) {
                            hpJoin = "(" + mzJoin + ")";
                        } else if (zyJoin.length() > 0) {
                            hpJoin = "(" + zyJoin + ")";
                        }
                    }
                }

                if (join.length() > 0) {
                    join += " AND " + hpJoin;
                } else {
                    join = hpJoin;
                }
            }
        }

        //获取相关门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, queryParams);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            re = result.getDetailModelList();

            if (area != null && area.length() > 0) {
                //通过机构代码列表获取地区列表
                List<String> orgCodelist = new ArrayList<>();
                for (int i = 0; i < result.getDetailModelList().size(); i++) {
                    Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(i);
                    String orgCode = obj.get("org_code").toString();

                    if (!orgCodelist.contains(orgCode)) {
                        orgCodelist.add(orgCode);
                    }
                }
                List<MOrganization> orglist = organization.getOrgs(orgCodelist);

                //判断是否属于区域
                for (int i = re.size() - 1; i >= 0; i--) {
                    Map<String, Object> obj = re.get(i);
                    String orgCode = obj.get("org_code").toString();

                    for (MOrganization org : orglist) {
                        if (orgCode.equals(org.getOrgCode())) {
                            String areaCode = new Integer(org.getAdministrativeDivision()).toString();
                            areaCode = areaCode.substring(0, areaCode.length() - 2) + "00";
                            if (areaCode.equals(area)) {
                                re.remove(i);
                            }
                        }
                    }
                }
            }
        }

        return re;
    }

    /**
     * 通过代码获取疾病名称
     * @return
     */
    private Map<String, String> getDiseaseMap(String code)
    {
        Map<String, String> map = new HashMap<>();
        map.put("code",code);
        MIcd10Dict dict = ict10Dict.getIcd10DictValue(code);
        if(dict!=null)
        {
            map.put("name",dict.getName());
        }
        else{
            map.put("name",code);
        }

        return map;
    }

    //@患者就诊过的疾病
    public List<Map<String, String>> getPatientDisease(String demographicId) throws Exception {
        List<String> list = new ArrayList<>();
        List<Map<String, String>> healthProblemDictMapList = new ArrayList<>();
        //门诊诊断
        Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}");
        if (outpatient.getDetailModelList() != null && outpatient.getDetailModelList().size() > 0) {
            for (int i = 0; i < outpatient.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) outpatient.getDetailModelList().get(i);
                if (obj.containsKey(BasisConstant.mzzd)) {
                    String code =obj.get(BasisConstant.mzzd).toString();
                    healthProblemDictMapList.add(getDiseaseMap(code));
                }
            }
        }
        //住院诊断
        Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}");
        if (hospitalized.getDetailModelList() != null && hospitalized.getDetailModelList().size() > 0) {
            for (int i = 0; i < hospitalized.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) hospitalized.getDetailModelList().get(i);
                if (obj.containsKey(BasisConstant.zyzd)) {
                    String code =obj.get(BasisConstant.zyzd).toString();
                    healthProblemDictMapList.add(getDiseaseMap(code));
                }
            }
        }
        return healthProblemDictMapList;
    }


    //@患者就诊过的年份
    public List<String> getPatientYear(String demographicId) throws Exception {
        List<String> list = new ArrayList<>();
        //患者事件列表
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}");
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            for (int i = 0; i < result.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(i);
                String year = obj.get("event_date").toString().substring(0, 4);
                if (!list.contains(year)) {
                    list.add(year);
                }
            }
        }

        return list;
    }


    //@患者就诊过的地区
    public List<Map<String, String>> getPatientArea(String demographicId) throws Exception {
        List<Map<String, String>> organizationMapList = new ArrayList<>();

        //患者事件列表
        List<String> orgCodeList = new ArrayList<>();
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}");
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            for (int i = 0; i < result.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(i);
                String orgCode = obj.get("org_code").toString();

                if (!orgCodeList.contains(orgCode)) {
                    orgCodeList.add(orgCode);
                }
            }
        }

        //通过机构代码列表获取地区列表
        List<MOrganization> orgList = organization.getOrgs(orgCodeList);
        List<String> array = new ArrayList<>();
        for (MOrganization org : orgList) {
            Map<String, String> organizationMap = new HashMap<>();
            String areaCode = new Integer(org.getAdministrativeDivision()).toString();
            areaCode = areaCode.substring(0, areaCode.length() - 2) + "00"; //转换成市级代码
            if (!array.contains(areaCode)) {
                array.add(areaCode);
                organizationMap.put("code", areaCode);

                MGeographyDict area = addressClient.getAddressDictById(areaCode);
                if(area !=null)
                {
                    String areaName = area.getName();
                    organizationMap.put("name", areaName);
                }
                else{
                    organizationMap.put("name", areaCode);
                }
                organizationMapList.add(organizationMap);
            }
        }
        return organizationMapList;
    }

}
