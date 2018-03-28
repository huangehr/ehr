package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.ResourceClient;
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
    private ResourceClient resource; //资源服务

    public List medicationRecords(String demographicId, String filter, String date, String keyWord) throws Exception {
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
                                continue;
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

                            //追加诊断名称 start
                            String subQ1 = "{\"q\":\"profile_id:" + masterMap.get("rowkey") + "\"}";
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
                            if (!StringUtils.isEmpty(subMap.get("EHR_000131"))) {
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

    public Map<String, Object> medicationSub(String profileId) {
        Map<String, Object> resultMap = new HashMap<>();
        String masterQ = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop masterEnvelop = resource.getMasterData(masterQ, 1, 1, null);
        List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
        if (masterList.size() > 0) {
            Map<String, Object> event = masterList.get(0);
            Map<String, Object> baseInfo = new HashMap<>();
            //姓名
            baseInfo.put("name", event.get("patient_name") == null ? "" : event.get("patient_name"));
            //性别
            String gender = event.get("EHR_000019") == null ? "" : (String) event.get("EHR_000019");
            if (gender.equals("1")) {
                gender = "男";
            } else if (gender.equals("2")) {
                gender = "女";
            }
            baseInfo.put("gender", gender == null ? "未知" : gender);
            //出生日期
            String birthday = "";
            if (!StringUtils.isEmpty(event.get("EHR_000007"))) {
                birthday = (String) event.get("EHR_000007");
            }
            if (StringUtils.isEmpty(birthday) && !StringUtils.isEmpty(event.get("EHR_000320"))) {
                birthday = (String) event.get("EHR_000320");
            }
            baseInfo.put("birthday", birthday);
            //基本信息
            resultMap.put("base", baseInfo);
            //临床诊断
            resultMap.put("diagnosis", event.get("diagnosis"));
            //详情
            String subQ = "{\"q\":\"profile_id:" + profileId + " AND (rowkey:*HDSD00_83* OR rowkey:*HDSD00_84*)\"}";
            Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
            List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
            List<Map<String, Object>> dataList = new ArrayList<>();
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
                    dataList.add(dataMap);
                }
            }
            resultMap.put("details", dataList);
        }
        return resultMap;
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
