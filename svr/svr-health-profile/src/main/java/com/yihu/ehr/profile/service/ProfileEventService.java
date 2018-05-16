package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.profile.util.BasisConstant;
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
    private ResourceClient resource;
    @Autowired
    private RedisService redisService;

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
            } else if ("4".equals(blurryType)) { //检查 inspect
                query = "{\"q\":\"demographic_id:" + demographicId + " AND EHR_000318:*\"}";
            } else if ("5".equals(blurryType)) { //检验 examine
                query = "{\"q\":\"demographic_id:" + demographicId + " AND EHR_000353:*\"}";
            } else if ("6".equals(blurryType)) { //免疫 immunity
                query = "{\"q\":\"demographic_id:" + demographicId + " AND EHR_002443:*\"}";
            } else if ("3".equals(blurryType)){ //影像 imagery
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
                            String healthProblemName = "";
                            if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                                healthProblemName = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
                            } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                                String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                                for (String code : diagnosisCode) {
                                    String name = redisService.getIcd10Name(code);
                                    if (!StringUtils.isEmpty(name)) {
                                        healthProblemName += name + "、";
                                    }
                                }
                            } else if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                                healthProblemName = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
                            } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                                String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                                for (String code : _hpCode) {
                                    String name = redisService.getHealthProblem(code);
                                    if (!StringUtils.isEmpty(name)) {
                                        healthProblemName += name + "、";
                                    }
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
                    String healthProblemName = "";
                    if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                        healthProblemName = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
                    } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                        String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                        for (String code : diagnosisCode) {
                            String name = redisService.getIcd10Name(code);
                            if (!StringUtils.isEmpty(name)) {
                                healthProblemName += name + "、";
                            }
                        }
                    } else if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                        healthProblemName = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
                    } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                        String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                        for (String code : _hpCode) {
                            String name = redisService.getHealthProblem(code);
                            if (!StringUtils.isEmpty(name)) {
                                healthProblemName += name + "、";
                            }
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
            return resultList;
        }
        query = SimpleSolrQueryUtil.getQuery(filter, date, query);
        envelop = resource.getMasterData(query, 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList != null && eventList.size() > 0) {
            for (Map<String ,Object> temp : eventList) {
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
                String healthProblemName = "";
                if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                    healthProblemName = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
                } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                    String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                    for (String code : diagnosisCode) {
                        String name = redisService.getIcd10Name(code);
                        if (!StringUtils.isEmpty(name)) {
                            healthProblemName += name + "、";
                        }
                    }
                } else if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                    healthProblemName = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
                } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                    String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                    for (String code : _hpCode) {
                        String name = redisService.getHealthProblem(code);
                        if (!StringUtils.isEmpty(name)) {
                            healthProblemName += name + "、";
                        }
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
            //追加诊断名称 start
            String healthProblemName = "";
            if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                healthProblemName = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
            } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                for (String code : diagnosisCode) {
                    String name = redisService.getIcd10Name(code);
                    if (!StringUtils.isEmpty(name)) {
                        healthProblemName += name + "、";
                    }
                }
            } else if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                healthProblemName = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
            } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                for (String code : _hpCode) {
                    String name = redisService.getHealthProblem(code);
                    if (!StringUtils.isEmpty(name)) {
                        healthProblemName += name + "、";
                    }
                }
            }
            resultMap.put("healthProblemName", healthProblemName);
            //追加诊断名称 end
        }
        return resultMap;
    }

    /**
     * 获取半年内的就诊记录
     * @param demographicId
     * @return
     * @throws Exception
     */
    public Map<String, Object> recentVisits (String demographicId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
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
            //追加诊断名称 start
            String healthProblemName = "";
            if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                healthProblemName = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
            } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                for (String code : diagnosisCode) {
                    String name = redisService.getIcd10Name(code);
                    if (!StringUtils.isEmpty(name)) {
                        healthProblemName += name + "、";
                    }
                }
            } else if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                healthProblemName = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
            } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                for (String code : _hpCode) {
                    String name = redisService.getHealthProblem(code);
                    if (!StringUtils.isEmpty(name)) {
                        healthProblemName += name + "、";
                    }
                }
            }
            resultMap.put("healthProblemName", healthProblemName);
            //追加诊断名称 end
        }
        return resultMap;
    }

    public Map<String, Object> recentVisitsSub (String profileId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String q = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop envelop = resource.getMasterData(q, 1, 1, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList.size() > 0) {
            Map<String ,Object> temp = eventList.get(0);
            List<Map<String, Object>> diagnosisList = new ArrayList<>();
            if (!StringUtils.isEmpty(temp.get(BasisConstant.diagnosis))) {
                String [] diagnosisCode = ((String) temp.get(BasisConstant.diagnosis)).split(";");
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
                    diagnosisMap.put("healthProblemName", name);
                    diagnosisMap.put("count", envelop.getDetailModelList().size());
                    diagnosisList.add(diagnosisMap);
                }
            }
            resultMap.put("diagnosis", diagnosisList);
            resultMap.put("profileId", temp.get("rowkey"));
            resultMap.put("orgCode", temp.get("org_code"));
            resultMap.put("orgName", temp.get("org_name"));
            resultMap.put("demographicId", temp.get("demographic_id"));
            resultMap.put("cdaVersion", temp.get("cda_version"));
            resultMap.put("eventDate", temp.get("event_date"));
            resultMap.put("profileType", temp.get("profile_type"));
            resultMap.put("eventType", temp.get("event_type"));
            resultMap.put("eventNo", temp.get("event_no"));
            if (temp.get("event_type").equals("0")) { //门诊信息
                resultMap.put("department", temp.get("EHR_000082") == null ? "" : temp.get("EHR_000082") );
                resultMap.put("doctor", temp.get("EHR_000079") == null ? "" : temp.get("EHR_000079"));
                //诊断名称 start
                String diagnosticResult = "";
                if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                    diagnosticResult = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
                } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                    String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                    for (String code : diagnosisCode) {
                        String name = redisService.getIcd10Name(code);
                        if (!StringUtils.isEmpty(name)) {
                            diagnosticResult += name + "、";
                        }
                    }
                } else if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                    diagnosticResult = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
                } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                    String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                    for (String code : _hpCode) {
                        String name = redisService.getHealthProblem(code);
                        if (!StringUtils.isEmpty(name)) {
                            diagnosticResult += name + "、";
                        }
                    }
                }
                //诊断名称 end
                resultMap.put("diagnosticResult", diagnosticResult); //诊断结果
                resultMap.put("inspectResult", temp.get("EHR_002883") == null ? "" : temp.get("EHR_002883")); //检查名称
                resultMap.put("examineResult", temp.get("EHR_000352") == null ? "" : temp.get("EHR_000352")); //检验项目
            } else if (temp.get("event_type").equals("1")) { //住院信息
                resultMap.put("department", temp.get("EHR_000229") == null ? "" : temp.get("EHR_000229"));
                resultMap.put("doctor", temp.get("EHR_005072") == null ? "" : temp.get("EHR_005072"));
                resultMap.put("inResult", temp.get("EHR_000295") == null ? "" :  temp.get("EHR_000295")); //入院诊断
                resultMap.put("outResult", temp.get("EHR_000295") == null ? "" : temp.get("EHR_000295")); //出院诊断
                resultMap.put("treatmentResults", temp.get("EHR_000166") == null ? "" : temp.get("EHR_000166")); //治疗结果
                resultMap.put("dischargeInstructions", temp.get("EHR_000157") == null ? "" :  temp.get("EHR_000157")); //出院医嘱
            }
        }
        return resultMap;
    }

}
