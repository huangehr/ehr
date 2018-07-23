package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.family.ResourceCells;
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
     * @param filter
     *  1. key=value的形式，多个条件用;分隔
     * @param blurryType
     *  1. 针对需要对特殊档案类型进行查询的参数(0-门诊 1-住院 2-体检 3-影像 4-检查 5-检验 6-妇幼 7-免疫)
     *  2. 此处有值的话filter参数中不能再包含event_type
     * @param date
     *  1. {"start":"2018-01-01T00:00:00Z","end":"2018-02-01T00:00:00Z","month":"2018-03"}
     * @param searchParam
     *  1. 此参数只针对机构和诊断
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> visits (String demographicId, String filter, String blurryType, String date, String searchParam) throws Exception {
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
                Envelop envelop1 = resource.healthFile("profile_type=2;id_card_no=" + demographicId, "-event_date", 1, 1000);
                List<Map<String, Object>> list1 = envelop1.getDetailModelList();
                if (list1 != null && list1.size() > 0) {
                    for (Map<String ,Object> temp : list1) {
                        Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                        if (resultMap != null) {
                            resultMap.put("eventType", blurryType);
                            resultMap.put("mark", "mark"); //临时处理
                            resultList.add(resultMap);
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
                            Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                            if (resultMap != null) {
                                resultMap.put("eventType", blurryType);
                                resultMap.put("mark", subList.get(0).get("EHR_000316")); //检查报告单号
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
                            Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                            if (resultMap != null) {
                                resultMap.put("eventType", blurryType);
                                resultMap.put("mark", subList.get(0).get("EHR_000363")); //检验报告单号
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
                query = SimpleSolrQueryUtil.getQuery(filter, date, query);
                envelop = resource.getMasterData(query, 1, 1000, null);
                List<Map<String, Object>> eventList = envelop.getDetailModelList();
                if (eventList != null && eventList.size() > 0) {
                    for (Map<String, Object> temp : eventList) {
                        String subQ = "{\"q\":\"rowkey:" + temp.get("rowkey") + "$HDSB03_12$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        if (subList != null && subList.size() > 0) {
                            Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                            if (resultMap != null) {
                                resultMap.put("eventType", blurryType);
                                resultMap.put("mark", subList.get(0).get("EHR_002438")); //预防接种卡编号
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
            envelop = resource.getMasterData(query, 1, 1000, null);
            List<Map<String, Object>> eventList = envelop.getDetailModelList();
            if (eventList != null && eventList.size() > 0) {
                for (Map<String ,Object> temp : eventList) {
                    Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                    if (resultMap != null) {
                        resultMap.put("eventType", blurryType);
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
                    Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                    if (resultMap != null) {
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
    public Map<String, Object> recentVisit (String demographicId, Integer days) throws Exception {
        String q = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        Date before = DateUtils.addDays(now, days);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = dateFormat.format(before);
        String end = dateFormat.format(now);
        String date = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
        q = SimpleSolrQueryUtil.getQuery(null, date, q);
        Envelop envelop = resource.getMasterData(q, 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        for (Map<String, Object> temp : eventList) {
            if (temp.get(ResourceCells.EVENT_TYPE) != null && temp.get(ResourceCells.EVENT_TYPE).equals("2")) {
                continue;
            }
            return simpleEvent(temp, null);
        }
        return new HashMap<>();
    }

    /**
     * 获取半年内的就诊记录
     * @param demographicId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> recentVisits (String demographicId, Integer days) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String q = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        Date before = DateUtils.addDays(now, days);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String start = dateFormat.format(before);
        String end = dateFormat.format(DateUtils.addDays(now, 1));
        String date = "{\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
        q = SimpleSolrQueryUtil.getQuery(null, date, q);
        Envelop envelop = resource.getMasterData(q, 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        for (Map<String, Object> temp : eventList) {
            if (temp.get(ResourceCells.EVENT_TYPE) != null && temp.get(ResourceCells.EVENT_TYPE).equals("2")) {
                continue;
            }
            resultList.add(simpleEvent(temp, null));
        }
        return resultList;
    }

    public Map<String, Object> recentVisitsSub (String profileId) throws Exception {
        String q = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop envelop = resource.getMasterData(q, 1, 1, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList.size() > 0) {
            Map<String ,Object> temp = eventList.get(0);
            Map<String, Object> resultMap = simpleEvent(temp, null);
            List<Map<String, Object>> diagnosisList = new ArrayList<>();
            if (!StringUtils.isEmpty(temp.get(ResourceCells.DIAGNOSIS))) {
                String [] diagnosisCode = ((String) temp.get(ResourceCells.DIAGNOSIS)).split(";");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date now = calendar.getTime();
                Date before = DateUtils.addDays(now, -180);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                String start = dateFormat.format(before);
                String end = dateFormat.format(DateUtils.addDays(now, 1));
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
            String subQ = "{\"q\":\"profile_id:" + profileId + "\"}";
            Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
            List<Map<String, Object>> subList = subEnvelop.getDetailModelList(); //细表数据
            if (temp.get(ResourceCells.EVENT_TYPE).equals("0")) { //门诊信息
                resultMap.put("department", temp.get("EHR_000082") == null ? "" : temp.get("EHR_000082") );
                resultMap.put("doctor", temp.get("EHR_000079") == null ? "" : temp.get("EHR_000079"));
                resultMap.put("diagnosticResult", resultMap.get("healthProblemName")); //诊断结果
                //检查
                List<Map<String, Object>> inspectResult = new ArrayList<>();
                for (Map<String, Object> temp1 : subList) {
                    if (temp1.get(ResourceCells.ROWKEY).toString().contains("HDSD00_79")) {
                        if (temp1.get("EHR_002883") != null) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("name", temp1.get("EHR_002883"));
                            data.put("mark", temp1.get("EHR_000316")); //检查报告单号
                            inspectResult.add(data);
                        }
                    }
                }
                resultMap.put("inspectResult", inspectResult);
                //检验
                List<Map<String, Object>> examineResult = new ArrayList<>();
                for (Map<String, Object> temp1 : subList) {
                    if (temp1.get(ResourceCells.ROWKEY).toString().contains("HDSD00_77")) {
                        if (temp1.get("EHR_000352") != null) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("name", temp1.get("EHR_000352"));
                            data.put("mark", temp1.get("EHR_000363")); //检验报告单号
                            examineResult.add(data);
                        }
                    }
                }
                resultMap.put("examineResult", examineResult);
            } else if (temp.get(ResourceCells.EVENT_TYPE).equals("1")) { //住院信息
                String department = "";
                if (!StringUtils.isEmpty(temp.get("EHR_006209"))) {
                    department = (String) temp.get("EHR_006209");
                }
                if (StringUtils.isEmpty(department) && !StringUtils.isEmpty("EHR_000228_VALUE")) {
                    department = (String) temp.get("EHR_000228_VALUE");
                }
                if (StringUtils.isEmpty(department) && !StringUtils.isEmpty("EHR_000228")) {
                    department = (String) temp.get("EHR_000228");
                }
                resultMap.put("department", department);
                String doctor = "";
                for (Map<String, Object> temp1 : subList) {
                    if (temp1.get(ResourceCells.ROWKEY).toString().contains("HDSD00_11")) {
                        if (!StringUtils.isEmpty(temp1.get("EHR_005072"))) {
                            doctor = (String) temp1.get("EHR_005072");
                            break;
                        }
                    }
                }
                resultMap.put("doctor", doctor);
                resultMap.put("inSituation", temp.get("EHR_005203") == null ? "" : temp.get("EHR_005203")); //入院情况
                resultMap.put("outSituation", temp.get("EHR_000154") == null ? "" : temp.get("EHR_000154")); //出院情况
                String inResult = "";
                String outResult = "";
                for (Map<String, Object> temp1 : subList) {
                    if (temp1.get(ResourceCells.ROWKEY).toString().contains("HDSD00_69")) {
                        if (temp1.get("EHR_006081") != null) {
                            if (temp1.get("EHR_006081").equals("入院诊断")) {
                                inResult = temp1.get("EHR_000293_VALUE") != null ?  (String) temp1.get("EHR_000293_VALUE") :  (String) temp1.get("EHR_000293");
                            }
                            if (temp1.get("EHR_006081").equals("出院诊断")) {
                                outResult = temp1.get("EHR_000293_VALUE") != null ?  (String) temp1.get("EHR_000293_VALUE") :  (String) temp1.get("EHR_000293");
                            }
                        }
                    }
                }
                resultMap.put("inResult", inResult); //入院诊断
                resultMap.put("outResult", outResult); //出院诊断
                resultMap.put("treatmentResults", temp.get("EHR_000166") == null ? "" : temp.get("EHR_000166")); //治疗结果
                resultMap.put("dischargeInstructions", temp.get("EHR_000157") == null ? "" :  temp.get("EHR_000157")); //出院医嘱
            }
            return resultMap;
        }
        return new HashMap<>();
    }

}
