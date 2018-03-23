package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by progr1mmer on 2018/3/13.
 */
@Service
public class ProfileMedicationService {

    @Autowired
    private ResourceClient resource; //资源服务
    @Autowired
    private ObjectMapper objectMapper;

    public List medicationRecords(String demographicId, String hpCode, String date, String keyWord) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String masterQ;
        if (hpCode != null) {
            masterQ = "{\"q\":\"demographic_id:" + demographicId + " AND health_problem:*" +  hpCode + "*\"}";
        } else {
            masterQ = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        }
        masterQ = getQuery(date, masterQ);
        Envelop masterEnvelop = resource.getMasterData(masterQ, null, null, null);
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> masterMap : masterList) {
                String rowKey = (String) masterMap.get("rowkey");
                String subQ = "{\"q\":\"profile_id:" + rowKey + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
                Envelop subEnvelop = resource.getSubData(subQ, null, null, null);
                if (subEnvelop.isSuccessFlg()) {
                    List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                    if (subList.size() > 0) {
                        Map<String, Object> resultMap = new HashMap<>();
                        List<Object> dataList = new ArrayList<>();
                        for (Map<String, Object> subMap : subList) {
                            if (subMap.get("EHR_000100") != null) {
                                if (keyWord != null && subMap.get("EHR_000100") != null) {
                                    if (subMap.get("EHR_000100").toString().contains(keyWord)) {
                                        dataList.add(subMap.get("EHR_000100"));
                                    }
                                } else {
                                    dataList.add(subMap.get("EHR_000100"));
                                }
                            }
                            if (subMap.get("EHR_000131") != null) {
                                if (keyWord != null) {
                                    if (subMap.get("EHR_000131").toString().contains(keyWord)) {
                                        dataList.add(subMap.get("EHR_000131"));
                                    }
                                } else {
                                    dataList.add(subMap.get("EHR_000131"));
                                }
                            }
                        }
                        if (dataList.size() > 0) {
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
                            //用药记录追加字段
                            resultMap.put("data", dataList);
                            resultList.add(resultMap);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    public Map<String, Integer> medicationRanking(String demographicId, String hpCode) {
        String masterQ;
        if (hpCode != null) {
            masterQ = "{\"q\":\"demographic_id:" + demographicId + " AND health_problem:*" +  hpCode + "*\"}";
        } else {
            masterQ = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        }
        Map<String, Integer> dataMap = new HashMap<>();
        Envelop masterEnvelop = resource.getMasterData(masterQ, null, null, null);
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> masterMap : masterList) {
                String rowKey = (String) masterMap.get("rowkey");
                String subQ = "{\"q\":\"profile_id:" + rowKey + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
                Envelop subEnvelop = resource.getSubData(subQ, null, null, null);
                if (subEnvelop.isSuccessFlg()) {
                    List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                    if (subList.size() > 0) {
                        for (Map<String, Object> subMap : subList) {
                            if (subMap.get("EHR_000100") != null) {
                                String drugName = (String) subMap.get("EHR_000100");
                                if (dataMap.containsKey(drugName)) {
                                    Integer count = dataMap.get(drugName);
                                    dataMap.put(drugName, count + 1);
                                } else {
                                    dataMap.put(drugName, 1);
                                }
                            }
                            if (subMap.get("EHR_000131") != null) {
                                String drugName = (String) subMap.get("EHR_000131");
                                if (dataMap.containsKey(drugName)) {
                                    Integer count = dataMap.get(drugName);
                                    dataMap.put(drugName, count + 1);
                                } else {
                                    dataMap.put(drugName, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return sortByValue(dataMap);
    }

    public List medicationSub(String profileId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String subQ = "{\"q\":\"profile_id:" + profileId + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
        Envelop subEnvelop = resource.getSubData(subQ, null, null, null);
        if (subEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
            if (subList.size() > 0) {
                for (Map<String, Object> subMap : subList) {
                    Map<String, Object> dataMap = new HashMap<>();
                    //String rowKey = (String) subMap.get("rowkey");
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
                    dataMap.put("drugUseTotalDoseUnit", subMap.get("EHR_001249")); //药物使用总剂量单位
                    resultList.add(dataMap);
                }
            }
        }
        return resultList;
    }

    private String getQuery(String date, String q) throws Exception {
        Map<String, String> qMap = objectMapper.readValue(q, Map.class);
        String param = qMap.get("q");
        if (date != null) {
            Map<String, String> dateMap = objectMapper.readValue(date, Map.class);
            if (dateMap.containsKey("start")) {
                param += " AND event_date:[" + dateMap.get("start") + " TO *]";
            }
            if (dateMap.containsKey("end")) {
                param += " AND event_date:[* TO " + dateMap.get("end") + "]";
            }
        }
        qMap.put("q", param);
        return objectMapper.writeValueAsString(qMap);
    }

    private Map<String, Integer> sortByValue(Map<String, Integer> sourceMap) {
        if (sourceMap == null || sourceMap.isEmpty()) {
            return sourceMap;
        }
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(sourceMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();
        Map.Entry<String, Integer> tmpEntry = null;
        while (iterator.hasNext()) {
            tmpEntry = iterator.next();
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
