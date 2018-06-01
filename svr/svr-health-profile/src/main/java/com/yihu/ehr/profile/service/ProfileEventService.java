package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.util.BasicConstant;
import com.yihu.ehr.profile.util.SimpleSolrQueryUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hzp 2016-06-27
 */
@Service
public class ProfileEventService extends ProfileBasicService {

    /**
     *
     * @param demographicId
     * @param filter key=value的形式，多个条件用;分隔
     * @param blurryType
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
            } else if ("2".equals(blurryType)) { //体检 medicalExam
                query = "{\"q\":\"demographic_id:" + demographicId + " AND event_type:2\"}";
            } else if ("3".equals(blurryType)) { //影像 imagery
                query = SimpleSolrQueryUtil.getQuery(filter, date, query);
                envelop = resource.getMasterData(query, 1, 1000, null);
                List<Map<String, Object>> masterList = envelop.getDetailModelList();
                if (masterList != null && masterList.size() > 0) {
                    for (Map<String ,Object> temp : masterList) {
                        String masterRowKey = (String) temp.get("rowkey");
                        String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSD00_19_02$*" + "\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        if (subList != null && subList.size() > 0) {
                            Map<String, Object> resultMap = simpleEvent(temp);
                            resultMap.put("eventType", blurryType);
                            resultMap.put("mark", "mark"); //临时处理
                            String healthProblemName = (String) resultMap.get("healthProblemName");
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
            } else if ("4".equals(blurryType)) { //检查 inspect
                query = SimpleSolrQueryUtil.getQuery(filter, date, query);
                envelop = resource.getMasterData(query, 1, 1000, null);
                List<Map<String, Object>> eventList = envelop.getDetailModelList();
                if (eventList != null && eventList.size() > 0) {
                    for (Map<String, Object> temp : eventList) {
                        String subQ = "{\"q\":\"rowkey:" + temp.get("rowkey") + "$HDSD00_79$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        if (subList != null && subList.size() > 0) {
                            Map<String, Object> resultMap = simpleEvent(temp);
                            resultMap.put("eventType", blurryType);
                            resultMap.put("mark", subList.get(0).get("EHR_000316")); //检查报告单号
                            String healthProblemName = (String) resultMap.get("healthProblemName");
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
            } else if ("5".equals(blurryType)) { //检验 examine
                query = SimpleSolrQueryUtil.getQuery(filter, date, query);
                envelop = resource.getMasterData(query, 1, 1000, null);
                List<Map<String, Object>> eventList = envelop.getDetailModelList();
                if (eventList != null && eventList.size() > 0) {
                    for (Map<String, Object> temp : eventList) {
                        String subQ = "{\"q\":\"rowkey:" + temp.get("rowkey") + "$HDSD00_77$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        if (subList != null && subList.size() > 0) {
                            Map<String, Object> resultMap = simpleEvent(temp);
                            resultMap.put("eventType", blurryType);
                            resultMap.put("mark", subList.get(0).get("EHR_000363")); //检验报告单号
                            String healthProblemName = (String) resultMap.get("healthProblemName");
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
            } else if ("6".equals(blurryType)) { //妇幼 immunity
                //query = "{\"q\":\"demographic_id:" + demographicId + " AND EHR_002443:*\"}";
                return resultList;
            } else if ("7".equals(blurryType)){  //免疫 immunity
                query = "{\"q\":\"demographic_id:" + demographicId + " AND EHR_002443:*\"}";
            } else {
                return resultList;
            }
            query = SimpleSolrQueryUtil.getQuery(filter, date, query);
            envelop = resource.getMasterData(query, 1, 1000, null);
            List<Map<String, Object>> eventList = envelop.getDetailModelList();
            if (eventList != null && eventList.size() > 0) {
                for (Map<String ,Object> temp : eventList) {
                    Map<String, Object> resultMap = simpleEvent(temp);
                    resultMap.put("eventType", blurryType);
                    String healthProblemName = (String) resultMap.get("healthProblemName");
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
            return resultList;
        } else {
            query = SimpleSolrQueryUtil.getQuery(filter, date, query);
            envelop = resource.getMasterData(query, 1, 1000, null);
            List<Map<String, Object>> eventList = envelop.getDetailModelList();
            if (eventList != null && eventList.size() > 0) {
                for (Map<String ,Object> temp : eventList) {
                    Map<String, Object> resultMap = simpleEvent(temp);
                    String healthProblemName = (String) resultMap.get("healthProblemName");
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
        String q = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        Date before = DateUtils.addDays(now, -30);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = dateFormat.format(before);
        String end = dateFormat.format(now);
        String date = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
        q = SimpleSolrQueryUtil.getQuery(null, date, q);
        Envelop envelop = resource.getMasterData(q, 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        for (Map<String, Object> temp : eventList) {
            if (temp.get(BasicConstant.eventType) != null && temp.get(BasicConstant.eventType).equals("2")) {
                continue;
            }
            return simpleEvent(temp);
        }
        return new HashMap<>();
    }

    /**
     * 获取半年内的就诊记录
     * @param demographicId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> recentVisits (String demographicId) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String q = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        Date before = DateUtils.addDays(now, -180);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = dateFormat.format(before);
        String end = dateFormat.format(now);
        String date = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
        q = SimpleSolrQueryUtil.getQuery(null, date, q);
        Envelop envelop = resource.getMasterData(q, 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        for (Map<String, Object> temp : eventList) {
            if (temp.get(BasicConstant.eventType) != null && temp.get(BasicConstant.eventType).equals("2")) {
                continue;
            }
            resultList.add(simpleEvent(temp));
        }
        return resultList;
    }

    public Map<String, Object> recentVisitsSub (String profileId) throws Exception {
        String q = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop envelop = resource.getMasterData(q, 1, 1, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList.size() > 0) {
            Map<String ,Object> temp = eventList.get(0);
            Map<String, Object> resultMap = simpleEvent(temp);
            List<Map<String, Object>> diagnosisList = new ArrayList<>();
            if (!StringUtils.isEmpty(temp.get(BasicConstant.diagnosis))) {
                String [] diagnosisCode = ((String) temp.get(BasicConstant.diagnosis)).split(";");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date now = calendar.getTime();
                Date before = DateUtils.addDays(now, -180);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                String start = dateFormat.format(before);
                String end = dateFormat.format(now);
                String date = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
                for (String code : diagnosisCode) {
                    q = SimpleSolrQueryUtil.getQuery("diagnosis?" + code, date, q);
                    envelop = resource.getMasterData(q, 1, 500, null);
                    String name = redisService.getIcd10Name(code);
                    Map<String, Object> diagnosisMap = new HashMap<>();
                    diagnosisMap.put("healthProblemName", name == null ? "自定义" : name);
                    diagnosisMap.put("count", envelop.getDetailModelList().size());
                    diagnosisList.add(diagnosisMap);
                }
            }
            resultMap.put("diagnosis", diagnosisList);
            if (temp.get(BasicConstant.eventType).equals("0")) { //门诊信息
                resultMap.put("department", temp.get("EHR_000082") == null ? "" : temp.get("EHR_000082") );
                resultMap.put("doctor", temp.get("EHR_000079") == null ? "" : temp.get("EHR_000079"));
                resultMap.put("diagnosticResult", resultMap.get("healthProblemName")); //诊断结果
                //检查
                String inspectResultQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_79$*\"}";
                envelop = resource.getSubData(inspectResultQ, 1, 500, null);
                List<Map<String, Object>> inspectResult = new ArrayList<>();
                List<Map<String, Object>> inspectResultList = envelop.getDetailModelList();
                for (Map<String, Object> temp1 : inspectResultList) {
                    if (temp1.get("EHR_002883") != null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", temp1.get("EHR_002883"));
                        data.put("mark", temp1.get("EHR_000316")); //检查报告单号
                        inspectResult.add(data);
                    }
                }
                resultMap.put("inspectResult", inspectResult);
                //检验
                String examineResultQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_77$*\"}";
                envelop = resource.getSubData(examineResultQ, 1, 500, null);
                List<Map<String, Object>> examineResult = new ArrayList<>();
                List<Map<String, Object>> examineResultList = envelop.getDetailModelList();
                for (Map<String, Object> temp1 : examineResultList) {
                    if (temp1.get("EHR_000352") != null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", temp1.get("EHR_000352"));
                        data.put("mark", temp1.get("EHR_000363")); //检验报告单号
                        examineResult.add(data);
                    }
                }
                resultMap.put("examineResult", examineResult);
            } else if (temp.get("event_type").equals("1")) { //住院信息
                resultMap.put("department", temp.get("EHR_000229") == null ? "" : temp.get("EHR_000229"));
                resultMap.put("doctor", temp.get("EHR_005072") == null ? "" : temp.get("EHR_005072"));
                resultMap.put("inSituation", temp.get("EHR_005203") == null ? "" : temp.get("EHR_005203")); //入院情况
                resultMap.put("outSituation", temp.get("EHR_000154") == null ? "" : temp.get("EHR_000154")); //出院情况
                resultMap.put("inResult", temp.get("EHR_000295") == null ? "" :  temp.get("EHR_000295")); //入院诊断
                resultMap.put("outResult", temp.get("EHR_000295") == null ? "" : temp.get("EHR_000295")); //出院诊断
                resultMap.put("treatmentResults", temp.get("EHR_000166") == null ? "" : temp.get("EHR_000166")); //治疗结果
                resultMap.put("dischargeInstructions", temp.get("EHR_000157") == null ? "" :  temp.get("EHR_000157")); //出院医嘱
            }
            return resultMap;
        }
        return new HashMap<>();
    }

}
