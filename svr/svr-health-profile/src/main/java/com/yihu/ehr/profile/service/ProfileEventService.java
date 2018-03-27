package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.profile.util.SimpleSolrQueryUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hzp 2016-06-27
 */
@Service
public class ProfileEventService {

    @Value("${spring.application.id}")
    private String appId;
    @Autowired
    private ResourceClient resource; //资源服务

    //event_type对应数据集
    Map<String,String[]> eventTypeDataset = new HashMap() {
        {
            put("3", new String[]{"HDSC01_04","HDSC01_08"}); //处方
            put("4", new String[]{"HDSC02_11","HDSC02_12"}); //医嘱
            put("5", new String[]{"HDSD01_01","HDSD02_01"}); //检查检验
        }
    };

    /**
     *
     * @param demographicId
     * @param filter key=value的形式，多个条件用;分隔
     * @param blurryType 0-门诊 2-影像 1-住院 3-体检 4-检验 6-免疫
     * @param date
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getPatientEvents(String demographicId, String filter, String blurryType, String date, String searchParam) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
        String query = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Envelop envelop;
        if (!StringUtils.isEmpty(blurryType)) {
            if ("0".equals(blurryType)) { //门诊 clinic
                query = "{\"q\":\"demographic_id:" + demographicId + " AND event_type:0\"}";
            } else if ("1".equals(blurryType)) { //住院 resident
                query = "{\"q\":\"demographic_id:" + demographicId + " AND event_type:1\"}";
            } else if ("3".equals(blurryType)) { //体检 medicalExam
                query = "{\"q\":\"demographic_id:" + demographicId + " AND event_type:2\"}";
            } else if ("4".equals(blurryType)) { //检验 inspect
                query = "{\"q\":\"demographic_id:" + demographicId + " AND (EHR_000318:* OR EHR_000353:*)\"}";
            } else if ("6".equals(blurryType)) { //免疫 immunity
                query = "{\"q\":\"demographic_id:" + demographicId + " AND EHR_002443:*\"}";
            } else if ("2".equals(blurryType)){ //影像 imagery
                query = SimpleSolrQueryUtil.getQuery(filter, date, query);
                envelop = resource.getMasterData(query, 1, 500, null);
                List<Map<String, Object>> masterList = envelop.getDetailModelList();
                if (masterList != null && masterList.size() > 0) {
                    for (Map<String ,Object> temp : masterList) {
                        String masterRowKey = (String) temp.get("rowkey");
                        String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSD00_19_02$*" + "\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        if (subList != null && subList.size() > 0) {
                            Map<String, Object> resultMap = new HashMap<>();
                            resultMap.put("profileId", temp.get("rowkey"));
                            resultMap.put("orgCode", temp.get("org_code"));
                            resultMap.put("orgName", temp.get("org_name"));
                            resultMap.put("demographicId", temp.get("demographic_id"));
                            resultMap.put("cdaVersion", temp.get("cda_version"));
                            resultMap.put("eventDate", temp.get("event_date"));
                            resultMap.put("profileType", temp.get("profile_type"));
                            resultMap.put("eventType", temp.get("event_type"));
                            resultMap.put("eventNo", temp.get("event_no"));
                            //追加诊断名称 start
                            String subQ1 = "{\"q\":\"profile_id:" + temp.get("rowkey") + "\"}";
                            Envelop subEnvelop1 = resource.getSubData(subQ1, 1, 500, null);
                            List<Map<String, Object>> subEventList = subEnvelop1.getDetailModelList();
                            String healthProblemName = "";
                            //根据诊断名称或根据字典值进行取值
                            for (Map<String ,Object> temp2 : subEventList) {
                                String diagnosis = "";
                                if (!StringUtils.isEmpty(temp2.get("EHR_000112")) || !StringUtils.isEmpty(temp2.get("EHR_000109_VALUE"))) {
                                    diagnosis = temp2.get("EHR_000112") != null ? (String) temp2.get("EHR_000112") : (String) temp2.get("EHR_000109_VALUE");
                                }
                                if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000295")) || !StringUtils.isEmpty(temp2.get("EHR_000293_VALUE")))) {
                                    diagnosis = temp2.get("EHR_000295") != null ? (String) temp2.get("EHR_000295") : (String) temp2.get("EHR_000293_VALUE");
                                }
                                if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000820")) || !StringUtils.isEmpty(temp2.get("EHR_000819_VALUE")))) {
                                    diagnosis = temp2.get("EHR_000820") != null ? (String) temp2.get("EHR_000820") : (String) temp2.get("EHR_000819_VALUE");
                                }
                                if (!StringUtils.isEmpty(diagnosis)) {
                                    healthProblemName += diagnosis + "、";
                                }
                            }
                            resultMap.put("healthProblemName", healthProblemName);
                            //追加诊断名称 end
                            if (!StringUtils.isEmpty(searchParam)) {
                                String orgName = (String) temp.get("org_name");
                                if (orgName.contains(searchParam) || healthProblemName.contains(searchParam)) {
                                    resultList.add(resultMap);
                                }
                            } else {
                                resultList.add(resultMap);
                            }
                        }
                    }
                }
                return resultList;
            } else {
                return resultList;
            }
            query = SimpleSolrQueryUtil.getQuery(filter, date, query);
            envelop = resource.getMasterData(query, 1, 500, null);
            List<Map<String, Object>> eventList = envelop.getDetailModelList();
            if (eventList != null && eventList.size() > 0) {
                for (Map<String ,Object> temp : eventList) {
                    for (int i = 1; i < 10; i ++) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("profileId", temp.get("rowkey"));
                        resultMap.put("orgCode", temp.get("org_code"));
                        resultMap.put("orgName", temp.get("org_name"));
                        resultMap.put("demographicId", temp.get("demographic_id"));
                        resultMap.put("cdaVersion", temp.get("cda_version"));
                        //resultMap.put("eventDate", temp.get("event_date"));
                        resultMap.put("eventDate", "2017-0" + i +  "-28T21:01:58Z");
                        resultMap.put("profileType", temp.get("profile_type"));
                        resultMap.put("eventType", temp.get("event_type"));
                        resultMap.put("eventNo", temp.get("event_no"));
                        //追加诊断名称 start
                        String subQ = "{\"q\":\"profile_id:" + temp.get("rowkey") + "\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                        List<Map<String, Object>> subEventList = subEnvelop.getDetailModelList();
                        String healthProblemName = "";
                        //根据诊断名称或根据字典值进行取值
                        for (Map<String ,Object> temp2 : subEventList) {
                            String diagnosis = "";
                            if (!StringUtils.isEmpty(temp2.get("EHR_000112")) || !StringUtils.isEmpty(temp2.get("EHR_000109_VALUE"))) {
                                diagnosis = temp2.get("EHR_000112") != null ? (String) temp2.get("EHR_000112") : (String) temp2.get("EHR_000109_VALUE");
                            }
                            if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000295")) || !StringUtils.isEmpty(temp2.get("EHR_000293_VALUE")))) {
                                diagnosis = temp2.get("EHR_000295") != null ? (String) temp2.get("EHR_000295") : (String) temp2.get("EHR_000293_VALUE");
                            }
                            if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000820")) || !StringUtils.isEmpty(temp2.get("EHR_000819_VALUE")))) {
                                diagnosis = temp2.get("EHR_000820") != null ? (String) temp2.get("EHR_000820") : (String) temp2.get("EHR_000819_VALUE");
                            }
                            if (!StringUtils.isEmpty(diagnosis)) {
                                healthProblemName += diagnosis + "、";
                            }
                        }
                        resultMap.put("healthProblemName", healthProblemName);
                        //追加诊断名称 end
                        if (!StringUtils.isEmpty(searchParam)) {
                            String orgName = (String) temp.get("org_name");
                            if (orgName.contains(searchParam) || healthProblemName.contains(searchParam)) {
                                resultList.add(resultMap);
                            }
                        } else {
                            resultList.add(resultMap);
                        }
                    }
                }
            }
            return resultList;
        }
        query = SimpleSolrQueryUtil.getQuery(filter, date, query);
        envelop = resource.getMasterData(query, 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList != null && eventList.size() > 0) {
            for (Map<String ,Object> temp : eventList) {
                for (int i = 1; i < 10; i ++) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("profileId", temp.get("rowkey"));
                    resultMap.put("orgCode", temp.get("org_code"));
                    resultMap.put("orgName", temp.get("org_name"));
                    resultMap.put("demographicId", temp.get("demographic_id"));
                    resultMap.put("cdaVersion", temp.get("cda_version"));
                    //resultMap.put("eventDate", temp.get("event_date"));
                    resultMap.put("eventDate", "2017-0" + i +  "-28T21:01:58Z");
                    resultMap.put("profileType", temp.get("profile_type"));
                    resultMap.put("eventType", temp.get("event_type"));
                    resultMap.put("eventNo", temp.get("event_no"));
                    //追加诊断名称 start
                    String subQ = "{\"q\":\"profile_id:" + temp.get("rowkey") + "\"}";
                    Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                    List<Map<String, Object>> subEventList = subEnvelop.getDetailModelList();
                    String healthProblemName = "";
                    //根据诊断名称或根据字典值进行取值
                    for (Map<String ,Object> temp2 : subEventList) {
                        String diagnosis = "";
                        if (!StringUtils.isEmpty(temp2.get("EHR_000112")) || !StringUtils.isEmpty(temp2.get("EHR_000109_VALUE"))) {
                            diagnosis = temp2.get("EHR_000112") != null ? (String) temp2.get("EHR_000112") : (String) temp2.get("EHR_000109_VALUE");
                        }
                        if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000295")) || !StringUtils.isEmpty(temp2.get("EHR_000293_VALUE")))) {
                            diagnosis = temp2.get("EHR_000295") != null ? (String) temp2.get("EHR_000295") : (String) temp2.get("EHR_000293_VALUE");
                        }
                        if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000820")) || !StringUtils.isEmpty(temp2.get("EHR_000819_VALUE")))) {
                            diagnosis = temp2.get("EHR_000820") != null ? (String) temp2.get("EHR_000820") : (String) temp2.get("EHR_000819_VALUE");
                        }
                        if (!StringUtils.isEmpty(diagnosis)) {
                            healthProblemName += diagnosis + "、";
                        }
                    }
                    resultMap.put("healthProblemName", healthProblemName);
                    //追加诊断名称 end
                    if (!StringUtils.isEmpty(searchParam)) {
                        String orgName = (String) temp.get("org_name");
                        if (orgName.contains(searchParam) || healthProblemName.contains(searchParam)) {
                            resultList.add(resultMap);
                        }
                    } else {
                        resultList.add(resultMap);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 获取最近的一条就诊信息
     * @param demographicId
     * @return
     * @throws Exception
     */
    public Map<String, Object> recentMedicalEvents(String demographicId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("show", false);
        String q = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        Date before = DateUtils.addDays(now, -7);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = dateFormat.format(before);
        String end = dateFormat.format(now);
        String date = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
        //q = SimpleSolrQueryUtil.getQuery(null, date, null, q);
        Envelop envelop = resource.getMasterData(q, 1, 1, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList.size() > 0) {
            Map<String ,Object> temp = eventList.get(0);
            resultMap.put("profileId", temp.get("rowkey"));
            resultMap.put("orgCode", temp.get("org_code"));
            resultMap.put("orgName", temp.get("org_name"));
            resultMap.put("demographicId", temp.get("demographic_id"));
            resultMap.put("cdaVersion", temp.get("cda_version"));
            resultMap.put("eventDate", temp.get("event_date"));
            resultMap.put("profileType", temp.get("profile_type"));
            resultMap.put("eventType", temp.get("event_type"));
            resultMap.put("eventNo", temp.get("event_no"));
            String rowKey = (String) temp.get("rowkey");
            String subQ = "{\"q\":\"profile_id:" + rowKey + "\"}";
            Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
            List<Map<String, Object>> subEventList = subEnvelop.getDetailModelList();
            String healthProblemName = "";
            //根据诊断名称或根据字典值进行取值
            for (Map<String ,Object> temp2 : subEventList) {
                String diagnosis = "";
                if (!StringUtils.isEmpty(temp2.get("EHR_000112")) || !StringUtils.isEmpty(temp2.get("EHR_000109_VALUE"))) {
                    diagnosis = temp2.get("EHR_000112") != null ? (String) temp2.get("EHR_000112") : (String) temp2.get("EHR_000109_VALUE");
                }
                if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000295")) || !StringUtils.isEmpty(temp2.get("EHR_000293_VALUE")))) {
                    diagnosis = temp2.get("EHR_000295") != null ? (String) temp2.get("EHR_000295") : (String) temp2.get("EHR_000293_VALUE");
                }
                if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000820")) || !StringUtils.isEmpty(temp2.get("EHR_000819_VALUE")))) {
                    diagnosis = temp2.get("EHR_000820") != null ? (String) temp2.get("EHR_000820") : (String) temp2.get("EHR_000819_VALUE");
                }
                if (!StringUtils.isEmpty(diagnosis)) {
                    healthProblemName += diagnosis + "、";
                }
            }
            if (!StringUtils.isEmpty(healthProblemName)) {
                resultMap.put("show", true);
                resultMap.put("healthProblemName", healthProblemName);
            }
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 返回包含event_type对应数据的事件
     */
    private  List<String> geyProfileByEventType(List<String> rowkeys,String eventType) throws Exception {
        List<String> re = new ArrayList<>();
        if (rowkeys != null && rowkeys.size() > 0) {
            String rowkeyString = "";
            String datasetString = "";
            //筛选事件
            for (String rowkey:rowkeys) {
                if (rowkeyString.length()>0) {
                    rowkeyString += " OR profile_id:"+rowkey;
                } else {
                    rowkeyString = "profile_id:"+rowkey;
                }
            }
            //筛选对应数据集
            String[] datasets = eventTypeDataset.get(eventType);
            if (datasets!=null && datasets.length>0) {
                for (String dataset:datasets) {
                    if (datasetString.length()>0) {
                        datasetString += " OR rowkey:*"+dataset+"*";
                    } else {
                        datasetString = "rowkey:*"+dataset+"*";
                    }
                }
            } else {
                return re;
            }
            String query = "{\"groupFields\":\"profile_id\",\"q\":\"("+rowkeyString+") AND ("+datasetString+")\"}";
            Envelop result = resource.getSubStat(query.replace(" ","+"), null ,null);
            if (result.getDetailModelList()!=null && result.getDetailModelList().size()>0) {
                List<Map<String,Object>> list = result.getDetailModelList();
                for (Map<String,Object> map:list) {
                    re.add(map.get("profile_id").toString());
                }
            }
        }
        return re;
    }

    /*
     * 通过eventNo获取某次门诊住院记录
     */
    public Map<String, Object> getMedicalEvent(String orgCode,String eventNo) throws Exception {
        Map<String, Object> re = new HashMap<>();
        //获取相关门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, "*", "*", "{\"q\":\"org_code:"+orgCode+"+AND+event_no:" + eventNo + "\"}",1,1);
        if (result.getDetailModelList()!=null&& result.getDetailModelList().size()>0) {
            re = (Map<String, Object>)result.getDetailModelList().get(0);
        } else {
            throw new Exception("不存在该档案信息！（event_no:" + eventNo + "）");
        }
        return re;
    }

    /* //通过身份证获取相关rowkeys
    private String getProfileIds(String demographicId) throws Exception
    {
        String re = "";
        //获取相关门诊住院记录
        Envelop main = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",null,null);
        if(main.getDetailModelList() != null && main.getDetailModelList().size() > 0)
        {
            //主表rowkey条件
            StringBuilder rowkeys = new StringBuilder();
            for(Map<String,Object> map : (List<Map<String,Object>>)main.getDetailModelList())
            {
                if(rowkeys.length() > 0)
                {
                    rowkeys.append(" OR ");
                }
                rowkeys.append(BasisConstant.profileId + ":" + map.get("rowkey").toString());
            }

            re = "(" + rowkeys.toString() +")";
        }
        else{
            re = BasisConstant.profileId+":(NOT *)";
        }

        return re;
    }
    // 患者就诊过的疾病
    public List<Map<String, String>> getPatientDisease(String demographicId) throws Exception {
        //获取门诊住院记录
        String rowkeys = getProfileIds(demographicId);
        String queryParams = "{\"q\":\""+ rowkeys +"\"}";

        List<String> codeList = new ArrayList<>();
        //门诊诊断
        Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis, appId,null, queryParams.replace(' ','+'), null, null);
        if (outpatient.getDetailModelList() != null && outpatient.getDetailModelList().size() > 0) {
            for (int i = 0; i < outpatient.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) outpatient.getDetailModelList().get(i);
                if (obj.containsKey(BasisConstant.mzzd) && obj.get(BasisConstant.mzzd)!=null) {
                    String code =obj.get(BasisConstant.mzzd).toString();
                    if(!code.equals("null")&&!codeList.contains(code))
                    {
                        codeList.add(code);
                    }
                }
            }
        }
        //住院诊断
        Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis, appId, queryParams.replace(' ','+'),1,1000);
        if (hospitalized.getDetailModelList() != null && hospitalized.getDetailModelList().size() > 0) {
            for (int i = 0; i < hospitalized.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) hospitalized.getDetailModelList().get(i);
                if (obj.containsKey(BasisConstant.zyzd) && obj.get(BasisConstant.zyzd)!=null) {
                    String code =obj.get(BasisConstant.zyzd).toString();
                    if(!code.equals("null")&&!codeList.contains(code))
                    {
                        codeList.add(code);
                    }
                }
            }
        }
        return getDiseaseList(codeList);
    }



     //@患者就诊过的年份
    public List<String> getPatientYear(String demographicId) throws Exception {
        List<String> list = new ArrayList<>();
        //患者事件列表
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId,null, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
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



     //患者就诊过的地区
    public List<Map<String, String>> getPatientArea(String demographicId) throws Exception {
        List<Map<String, String>> organizationMapList = new ArrayList<>();

        //患者事件列表
        List<String> orgCodeList = new ArrayList<>();
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId,null, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
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

            String areaCode = new Integer(org.getAdministrativeDivision()).toString();
            areaCode = areaCode.substring(0, areaCode.length() - 2) + "00"; //转换成市级代码
            if (!array.contains(areaCode)) {
                array.add(areaCode);

            }
        }
        List<MGeographyDict> areaList = addressClient.getAddressDictByIdList(array);
        if(areaList!=null) {
            Map<String, String> organizationMap = new HashMap<>();
            for (int i = 0; i < areaList.size(); i++) {
                organizationMap.put("code", array.get(i));


                if(areaList.get(i) !=null)
                {
                    String areaName = areaList.get(i).getName();
                    organizationMap.put("name", areaName);
                }
                else{
                    organizationMap.put("name", array.get(i));
                }
                organizationMapList.add(organizationMap);
            }
        }
        return organizationMapList;
    }*/
}
