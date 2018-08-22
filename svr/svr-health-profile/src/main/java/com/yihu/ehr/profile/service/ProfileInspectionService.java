package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.InspectionInfo;
import com.yihu.ehr.profile.util.NumberValidationUtils;
import com.yihu.ehr.profile.util.SimpleSolrQueryUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by progr1mmer on 2018/3/13.
 */
@Service
public class ProfileInspectionService extends ProfileBasicService {

    public List inspectionRecords(String demographicId, String filter, String date, String searchParam) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("HDSD00_79", "inspect");
        typeMap.put("HDSD00_77", "examine");
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("HDSD00_79", "EHR_002883"); //检查名称
        nameMap.put("HDSD00_77", "EHR_000352"); //检验项目
        Map<String, String> numMap = new HashMap<>();
        numMap.put("HDSD00_79", "EHR_000316"); //检查报告单号
        numMap.put("HDSD00_77", "EHR_000363"); //检验报告单号
        for (String dataset : typeMap.keySet()) {
            String query = "{\"q\":\"demographic_id:" + demographicId + "\"}";
            query = SimpleSolrQueryUtil.getQuery(filter, date, query);
            Envelop masterEnvelop = resource.getMasterData(query, 1, 1000, null);
            if (masterEnvelop.isSuccessFlg()) {
                List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
                //循环获取结果集
                for (Map<String, Object> temp : masterList) {
                    String subQ = "{\"q\":\"rowkey:" + temp.get("rowkey") + "$" + dataset + "$*\"}";
                    Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                    List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                    subList.forEach(item -> {
                        Map<String, Object> resultMap = simpleEvent(temp, searchParam);
                        if (resultMap != null) {
                            String healthProblemName = (String) resultMap.get("healthProblemName");
                            String itemName = (String) item.get(nameMap.get(dataset));
                            if (itemName != null) {
                                resultMap.put("projectName", itemName);
                            } else {
                                if (dataset.equals("HDSD00_79")) {
                                    resultMap.put("projectName", "检查报告-" + healthProblemName);
                                } else {
                                    resultMap.put("projectName", "检验报告-" + healthProblemName);
                                }
                            }
                            resultMap.put("type", typeMap.get(dataset));
                            resultMap.put("mark", item.get(numMap.get(dataset)));
                            resultList.add(resultMap);
                        }
                    });
                }
            }
        }
        return resultList;
    }

    public List<InspectionInfo> inspectionStatistics(String demographicId, String table) throws Exception {
        String query = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        query = SimpleSolrQueryUtil.getQuery(null, null, query);
        Envelop masterEnvelop = resource.getMasterData(query, 1, 1000, null);
        Map<String, Integer> dataMap = new HashMap<>();
        Map<String, String> firstMap = new HashMap<>();
        Map<String, String> lastMap = new HashMap<>();
        Map<String, Integer> exMap = new HashMap<>();
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> temp : masterList) {
                String subQ = "{\"q\":\"rowkey:" + temp.get("rowkey") + "$" + table + "$*\"}";
                Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                for (int i = 0; i < subList.size(); i ++) {
                    Map<String, Object> item = subList.get(i);
                    if (!StringUtils.isEmpty(item.get("EHR_002883"))) {  //检查名称
                        String name = item.get("EHR_002883").toString();
                        String result = item.get("EHR_000347") == null ? "-" : item.get("EHR_000347").toString(); //影像结论
                        if (dataMap.containsKey(name)) {
                            Integer count = dataMap.get(name);
                            dataMap.put(name, count + 1);
                            lastMap.put(name, temp.get(ResourceCells.EVENT_DATE) + "&" + temp.get(ResourceCells.ORG_NAME) + "&" + item.get(ResourceCells.ROWKEY) + "&" + result);
                        } else {
                            dataMap.put(name, 1);
                            firstMap.put(name, temp.get(ResourceCells.EVENT_DATE) + "&" + temp.get(ResourceCells.ORG_NAME) + "&" + item.get(ResourceCells.ROWKEY) + "&" + result);
                        }
                        if (!StringUtils.isEmpty(item.get("EHR_000344"))) {
                            String ex = item.get("EHR_000344").toString();
                            if ("1".equals(ex)) {
                                if (exMap.containsKey(name)) {
                                    Integer count = exMap.get(name);
                                    exMap.put(name, count + 1);
                                } else {
                                    exMap.put(name, 1);
                                }
                            }
                        }
                    } else if (!StringUtils.isEmpty(item.get("EHR_000352"))) { //检验项目
                        String name = item.get("EHR_000352").toString();
                        if (dataMap.containsKey(name)) {
                            Integer count = dataMap.get(name);
                            dataMap.put(name, count + 1);
                            lastMap.put(name, temp.get(ResourceCells.EVENT_DATE) + "&" + temp.get(ResourceCells.ORG_NAME) + "&" + item.get(ResourceCells.ROWKEY) + "&" + "-");
                        } else {
                            dataMap.put(name, 1);
                            firstMap.put(name, temp.get(ResourceCells.EVENT_DATE) + "&" + temp.get(ResourceCells.ORG_NAME) + "&" + item.get(ResourceCells.ROWKEY) + "&" + "-");
                        }
                    }
                }
            }
        }
        Map<String, Integer> newDataMap = sortByValue(dataMap);
        List<InspectionInfo> inspectionInfos = new ArrayList<>();
        newDataMap.forEach((key, val) -> {
            InspectionInfo inspectionInfo = new InspectionInfo();
            inspectionInfo.setName(key);
            inspectionInfo.setCount(val);
            inspectionInfo.setFirstTime(firstMap.get(key).split("&")[0]);
            if (exMap.containsKey(key)) {
                inspectionInfo.setExCount(exMap.get(key));
            }
            if (lastMap.get(key) != null) {
                inspectionInfo.setLastTime(lastMap.get(key).split("&")[0]);
                inspectionInfo.setLastOrg(lastMap.get(key).split("&")[1]);
                inspectionInfo.setLastRecord(lastMap.get(key).split("&")[2]);
                inspectionInfo.setResult(lastMap.get(key).split("&")[3]);
            } else {
                inspectionInfo.setLastTime(firstMap.get(key).split("&")[0]);
                inspectionInfo.setLastOrg(firstMap.get(key).split("&")[1]);
                inspectionInfo.setLastRecord(firstMap.get(key).split("&")[2]);
                inspectionInfo.setResult(firstMap.get(key).split("&")[3]);
            }
            inspectionInfos.add(inspectionInfo);
        });
        return inspectionInfos;
    }

    public Map<String, String> inspectionStatisticsOneSub(String profileId) throws Exception {
        Map<String, String> dataMap = new HashMap<>();
        String subQ = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop subData = resource.getSubData(subQ, 1, 1, null);
        List<Map<String, Object>> subList = subData.getDetailModelList();
        if (subList.size() > 0) {
            Map<String, Object> temp = subList.get(0);
            String reportNo = temp.get("EHR_000363").toString(); //检验报告单号
            subQ = "{\"q\":\"rowkey:" + profileId.split("\\$")[0] + "$HDSD00_75$*" + "\"}";
            subData = resource.getSubData(subQ, 1, 1000, null);
            List<Map<String, Object>> subItems = subData.getDetailModelList();
            subItems.forEach(item -> {
                if (reportNo.equals(item.get("EHR_006339"))) { //子项所属的报告单号
                    //结果值
                    if (item.get("EHR_000387") != null) {
                        String result = item.get("EHR_000387").toString(); //结果值
                        if (item.get("EHR_000391") != null && item.get("EHR_000390")!= null) {
                            String low = item.get("EHR_000391").toString(); //下限
                            String high = item.get("EHR_000390").toString(); //上限
                            if (NumberValidationUtils.isRealNumber(result) && NumberValidationUtils.isRealNumber(low) && NumberValidationUtils.isRealNumber(high)) {
                                Double _result = Double.parseDouble(result);
                                Double _row = Double.parseDouble(low);
                                Double _high = Double.parseDouble(high);
                                if (_result < _row) {
                                    dataMap.put(item.get("EHR_000394").toString(), "偏低");
                                } else if (_result > _high) {
                                    dataMap.put(item.get("EHR_000394").toString(), "偏高");
                                } else {
                                    dataMap.put(item.get("EHR_000394").toString(), "正常");
                                }
                            } else {
                                dataMap.put(item.get("EHR_000394").toString(), item.get("EHR_000387").toString());
                            }
                        } else {
                            dataMap.put(item.get("EHR_000394").toString(), item.get("EHR_000387").toString());
                        }
                    } else {
                        dataMap.put(item.get("EHR_000394").toString(), "");
                    }
                }
            });
        }
        return dataMap;
    }

    public Map<String, Map<String, Integer>> inspectionStatisticsAllSub(String demographicId, String name) throws Exception {
        String query = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        query = SimpleSolrQueryUtil.getQuery(null, null, query);
        Envelop masterEnvelop = resource.getMasterData(query, 1, 1000, null);
        List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
        //循环获取结果集
        Set<String> data = new HashSet<>();
        Map<String, Integer> lowMap = new HashMap<>();
        Map<String, Integer> highMap = new HashMap<>();
        Map<String, Integer> normalMap = new HashMap<>();
        Map<String, Integer> unknownMap = new HashMap<>();
        Map<String, Integer> positiveMap = new HashMap<>();
        Map<String, Integer> negativeMap = new HashMap<>();
        for (Map<String, Object> masterItem : masterList) {
            String subQ = "{\"q\":\"rowkey:" + masterItem.get("rowkey") + "$HDSD00_77$*\"}";
            Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
            List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
            for (Map<String, Object> subItem : subList) {
                if (name.equals(subItem.get("EHR_000352"))) { //检验项目
                    String reportNo = subItem.get("EHR_000363").toString(); //检验报告单号
                    subQ = "{\"q\":\"rowkey:" + masterItem.get(ResourceCells.ROWKEY) + "$HDSD00_75$*" + "\"}";
                    subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                    List<Map<String, Object>> subItems = subEnvelop.getDetailModelList();
                    subItems.forEach(item -> {
                        if (reportNo.equals(item.get("EHR_006339"))) { //子项所属的报告单号
                            String itemName = item.get("EHR_000394").toString(); //子项名称
                            data.add(itemName);
                            //结果值
                            if (item.get("EHR_000387") != null) { //结果值
                                String result = item.get("EHR_000387").toString();
                                if (item.get("EHR_000391") != null && item.get("EHR_000390")!= null) {
                                    String low = item.get("EHR_000391").toString(); //上限
                                    String high = item.get("EHR_000390").toString(); //下限
                                    if (NumberValidationUtils.isRealNumber(result) && NumberValidationUtils.isRealNumber(low) && NumberValidationUtils.isRealNumber(high)) {
                                        Double _result = Double.parseDouble(result);
                                        Double _row = Double.parseDouble(low);
                                        Double _high = Double.parseDouble(high);
                                        if (_result < _row) {
                                            if (lowMap.containsKey(itemName)) {
                                                lowMap.put(itemName, lowMap.get(itemName) + 1);
                                            } else {
                                                lowMap.put(itemName, 1);
                                            }
                                        } else if (_result > _high) {
                                            if (highMap.containsKey(itemName)) {
                                                highMap.put(itemName, highMap.get(itemName) + 1);
                                            } else {
                                                highMap.put(itemName, 1);
                                            }
                                        } else {
                                            if (normalMap.containsKey(itemName)) {
                                                normalMap.put(itemName, normalMap.get(itemName) + 1);
                                            } else {
                                                normalMap.put(itemName, 1);
                                            }
                                        }
                                    } else {
                                        if (unknownMap.containsKey(itemName)) {
                                            unknownMap.put(itemName, unknownMap.get(itemName) + 1);
                                        } else {
                                            unknownMap.put(itemName, 1);
                                        }
                                    }
                                } else if (item.get("EHR_000382") != null) { //参考值备注
                                    String range = item.get("EHR_000382").toString();
                                    if (range.split("-").length == 2) {
                                        String low = range.split("-")[0]; //上限
                                        String high = range.split("-")[1]; //下限
                                        if (NumberValidationUtils.isRealNumber(result) && NumberValidationUtils.isRealNumber(low) && NumberValidationUtils.isRealNumber(high)) {
                                            Double _result = Double.parseDouble(result);
                                            Double _row = Double.parseDouble(low);
                                            Double _high = Double.parseDouble(high);
                                            if (_result < _row) {
                                                if (lowMap.containsKey(itemName)) {
                                                    lowMap.put(itemName, lowMap.get(itemName) + 1);
                                                } else {
                                                    lowMap.put(itemName, 1);
                                                }
                                            } else if (_result > _high) {
                                                if (highMap.containsKey(itemName)) {
                                                    highMap.put(itemName, highMap.get(itemName) + 1);
                                                } else {
                                                    highMap.put(itemName, 1);
                                                }
                                            } else {
                                                if (normalMap.containsKey(itemName)) {
                                                    normalMap.put(itemName, normalMap.get(itemName) + 1);
                                                } else {
                                                    normalMap.put(itemName, 1);
                                                }
                                            }
                                        } else {
                                            if (unknownMap.containsKey(itemName)) {
                                                unknownMap.put(itemName, unknownMap.get(itemName) + 1);
                                            } else {
                                                unknownMap.put(itemName, 1);
                                            }
                                        }
                                    } else {
                                        if (unknownMap.containsKey(itemName)) {
                                            unknownMap.put(itemName, unknownMap.get(itemName) + 1);
                                        } else {
                                            unknownMap.put(itemName, 1);
                                        }
                                    }
                                } else  {
                                    if (result.equals("阳性")) {
                                        if (positiveMap.containsKey(itemName)) {
                                            positiveMap.put(itemName, positiveMap.get(itemName) + 1);
                                        } else {
                                            positiveMap.put(itemName, 1);
                                        }
                                    } else if (result.equals("阴性")) {
                                        if (negativeMap.containsKey(itemName)) {
                                            negativeMap.put(itemName, negativeMap.get(itemName) + 1);
                                        } else {
                                            negativeMap.put(itemName, 1);
                                        }
                                    } else {
                                        if (unknownMap.containsKey(itemName)) {
                                            unknownMap.put(itemName, unknownMap.get(itemName) + 1);
                                        } else {
                                            unknownMap.put(itemName, 1);
                                        }
                                    }
                                }
                            } else {
                                if (unknownMap.containsKey(itemName)) {
                                    unknownMap.put(itemName, unknownMap.get(itemName) + 1);
                                } else {
                                    unknownMap.put(itemName, 1);
                                }
                            }
                        }
                    });
                }
            }
        }
        Map<String, Map<String, Integer>> result = new HashMap<>();
        data.forEach(item -> {
            Map<String, Integer> details = new HashMap<>();
            if (lowMap.containsKey(item)) {
                details.put("偏低", lowMap.get(item));
            }
            if (highMap.containsKey(item)) {
                details.put("偏高", highMap.get(item));
            }
            if (normalMap.containsKey(item)) {
                details.put("正常", normalMap.get(item));
            }
            if (unknownMap.containsKey(item)) {
                details.put("未知", unknownMap.get(item));
            }
            if (positiveMap.containsKey(item)) {
                details.put("阳性", positiveMap.get(item));
            }
            if (negativeMap.containsKey(item)) {
                details.put("阴性", negativeMap.get(item));
            }
            result.put(item, details);
        });
        return result;
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

    public static void main(String [] args) {
        System.out.println(NumberValidationUtils.isRealNumber("121.2"));
    }

}
