package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.profile.util.BasisConstant;
import com.yihu.ehr.profile.util.SimpleSolrQueryUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by progr1mmer on 2018/3/13.
 */
@Service
public class ProfileMedicationService {

    @Autowired
    private ResourceClient resource;
    @Autowired
    private RedisService redisService;

    public Map<String, Integer> medicationRanking(String demographicId, String hpCode, String date) throws Exception {
        String masterQ;
        if (hpCode != null) {
            masterQ = "{\"q\":\"demographic_id:" + demographicId + " AND health_problem:*" +  hpCode + "*\"}";
        } else {
            masterQ = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        }
        masterQ = SimpleSolrQueryUtil.getQuery(null, date, masterQ);
        Map<String, Integer> dataMap = new HashMap<>();
        Envelop masterEnvelop = resource.getMasterData(masterQ, 1, 500, null);
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> masterMap : masterList) {
                String rowKey = (String) masterMap.get("rowkey");
                String subQ = "{\"q\":\"profile_id:" + rowKey + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
                Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                if (subEnvelop.isSuccessFlg()) {
                    List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                    if (subList.size() > 0) {
                        for (Map<String, Object> subMap : subList) {
                            if (!StringUtils.isEmpty(subMap.get("EHR_000131"))) {
                                String drugName = (String) subMap.get("EHR_000131");
                                if (dataMap.containsKey(drugName)) {
                                    Integer count = dataMap.get(drugName);
                                    dataMap.put(drugName, count + 1);
                                } else {
                                    dataMap.put(drugName, 1);
                                }
                                continue;
                            }
                            if (!StringUtils.isEmpty(subMap.get("EHR_000100"))) {
                                String drugName = (String) subMap.get("EHR_000100");
                                if (dataMap.containsKey(drugName)) {
                                    Integer count = dataMap.get(drugName);
                                    dataMap.put(drugName, count + 1);
                                } else {
                                    dataMap.put(drugName, 1);
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return sortByValue(dataMap);
    }

    public List<Map<String, Object>> medicationRecords(String demographicId, String filter, String date, String keyWord) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String masterQ = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        masterQ = SimpleSolrQueryUtil.getQuery(filter, date, masterQ);
        Envelop masterEnvelop = resource.getMasterData(masterQ, 1, 500, null);
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> masterMap : masterList) {
                String rowKey = (String) masterMap.get("rowkey");
                String subQ = "{\"q\":\"profile_id:" + rowKey + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
                Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                if (subEnvelop.isSuccessFlg()) {
                    List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                    if (subList.size() > 0) {
                        Map<String, Object> resultMap = new HashMap<>();
                        boolean match = false;
                        for (Map<String, Object> subMap : subList) {
                            if (subMap.get("EHR_000131") != null) {
                                if (keyWord != null) {
                                    if (subMap.get("EHR_000131").toString().contains(keyWord)) {
                                        match = true;
                                        break;
                                    }
                                } else {
                                    match = true;
                                    break;
                                }
                            }
                            if (subMap.get("EHR_000100") != null) {
                                if (keyWord != null && subMap.get("EHR_000100") != null) {
                                    if (subMap.get("EHR_000100").toString().contains(keyWord)) {
                                        match = true;
                                        break;
                                    }
                                } else {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (match) {
                            //时间轴基本字段
                            resultMap.put("profileId", masterMap.get("rowkey"));
                            resultMap.put("orgCode", masterMap.get("org_code"));
                            resultMap.put("orgName", masterMap.get("org_name"));
                            resultMap.put("demographicId", masterMap.get("demographic_id"));
                            resultMap.put("cdaVersion", masterMap.get("cda_version"));
                            resultMap.put("eventDate", masterMap.get("event_date"));
                            resultMap.put("profileType", masterMap.get("profile_type"));
                            resultMap.put("eventType", masterMap.get("event_type"));
                            resultMap.put("eventNo", masterMap.get("event_no"));
                            //追加诊断名称 start
                            String healthProblemName = "";
                            if (!StringUtils.isEmpty(masterMap.get("diagnosis_name"))) {
                                healthProblemName = ((String) masterMap.get("diagnosis_name")).replaceAll(";", "、");
                            } else if (!StringUtils.isEmpty(masterMap.get("diagnosis"))) {
                                String [] diagnosisCode = ((String) masterMap.get("diagnosis")).split(";");
                                for (String code : diagnosisCode) {
                                    String name = redisService.getIcd10Name(code);
                                    if (!StringUtils.isEmpty(name)) {
                                        healthProblemName += name + "、";
                                    }
                                }
                            } else if (!StringUtils.isEmpty(masterMap.get("health_problem_name"))) {
                                healthProblemName = ((String) masterMap.get("health_problem_name")).replaceAll(";", "、");
                            } else if (!StringUtils.isEmpty(masterMap.get("health_problem"))) {
                                String [] _hpCode = ((String) masterMap.get("health_problem")).split(";");
                                for (String code : _hpCode) {
                                    String name = redisService.getHealthProblem(code);
                                    if (!StringUtils.isEmpty(name)) {
                                        healthProblemName += name + "、";
                                    }
                                }
                            }
                            resultMap.put("healthProblemName", healthProblemName);
                            //追加诊断名称 end
                            if (masterMap.get("event_type").equals("0")) { //门诊信息
                                resultMap.put("department", masterMap.get("EHR_000082"));
                                resultMap.put("doctor", masterMap.get("EHR_000079"));
                            } else if (masterMap.get("event_type").equals("1")) { //住院信息
                                resultMap.put("department", masterMap.get("EHR_000229"));
                                resultMap.put("doctor", masterMap.get("EHR_005072"));
                            }
                            resultList.add(resultMap);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    public Envelop recentMedicationSub(String demographicId, String date, Integer page, Integer size) throws Exception {
        String masterQ = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        masterQ = SimpleSolrQueryUtil.getQuery(null, date, masterQ);
        Envelop masterEnvelop = resource.getMasterData(masterQ, 1, 500, null);
        List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Object eventDate = "";
        for (Map<String, Object> event : masterList) {
            //详情
            String subQ = "{\"q\":\"profile_id:" + event.get(BasisConstant.rowkey) + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
            Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
            List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
            if (subList.size() > 0) {
                for (Map<String, Object> subMap : subList) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("prescriptionNumber", subMap.get("EHR_000086")); //处方编号
                    dataMap.put("substancesForDrugUse", subMap.get("EHR_000101")); //药物使用次剂量
                    dataMap.put("prescriptionDrugGroupNumber", subMap.get("EHR_000127")); //处方药品组号
                    dataMap.put("drugSpecifications", subMap.get("EHR_000129")); //药物规格
                    dataMap.put("drugFormulationCode", subMap.get("EHR_000130")); //药物剂型代码
                    dataMap.put("drugFormulationValue", subMap.get("EHR_000130_VALUE")); //药物剂型值
                    dataMap.put("drugName", subMap.get("EHR_000131")); //药物名称
                    dataMap.put("drugsUseDosageUnits", subMap.get("EHR_000133")); //药物使用剂量单位
                    dataMap.put("drugUsageFrequencyCode", subMap.get("EHR_000134")); //药物使用频次代码
                    dataMap.put("drugUsageFrequencyValue", subMap.get("EHR_000134_VALUE")); //药物使用频次值
                    dataMap.put("totalDoseOfDrugUsed", subMap.get("EHR_000135")); //药物使用总剂量
                    dataMap.put("medicationRouteCode", subMap.get("EHR_000136")); //用药途径代码
                    dataMap.put("medicationRouteValue", subMap.get("EHR_000136_VALUE")); //用药途径值
                    String dataSetCode = String.valueOf(subMap.get("rowkey")).split("\\$")[1];
                    if ("HDSD00_84".equals(dataSetCode)) { //西药
                        dataMap.put("drugUseTotalDoseUnit", subMap.get("EHR_001249")); //药物使用总剂量单位
                    } else { //中药
                        dataMap.put("drugUseTotalDoseUnit", subMap.get("EHR_001250")); //药物使用总剂量单位
                    }
                    dataList.add(dataMap);
                }
            }
            if (dataList.size() > 0) {
                eventDate = event.get(BasisConstant.eventDate);
                break;
            }
        }
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        envelop.setTotalPage(dataList.size() % size > 0 ? dataList.size() / size + 1 : dataList.size() / size);
        envelop.setTotalCount(dataList.size());
        List result = new ArrayList();
        for (int i = (page - 1) * size; i < page * size; i ++) {
            if (i > dataList.size() - 1) {
                break;
            }
            result.add(dataList.get(i));
        }
        envelop.setDetailModelList(result);
        envelop.setObj(eventDate);
        return envelop;
    }

    private Map<String, Integer> sortByValue(Map<String, Integer> sourceMap) {
        if (sourceMap == null || sourceMap.isEmpty()) {
            return sourceMap;
        }
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(sourceMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> tmpEntry = iterator.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    class MapValueComparator implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> me1, Map.Entry<String, Integer> me2) {

            return - me1.getValue().compareTo(me2.getValue());
        }
    }

}
