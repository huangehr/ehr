package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by progr1mmer on 2018/1/7.
 */
@Service
public class ProfileDiseaseService extends ProfileBasicService {

    @Autowired
    private ProfileMedicationService profileMedicationService;

    /**
     * 根据就诊记录，获取慢病记录
     * @param demographicId
     * @return
     */
    public List<Map<String, Object>> getHealthProblem (String demographicId) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        //获取门诊住院记录
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        Map<String, List<Map<String, Object>>> hpMap = new HashedMap();
        if (eventList.size() > 0) {
            //进行降序
            Collections.sort(eventList, new ProfileDiseaseService.EventDateComparatorDesc());
            eventList.forEach(item -> {
                if (item.containsKey(ResourceCells.DIAGNOSIS)) {
                    String diagnosis = (String) item.get(ResourceCells.DIAGNOSIS);
                    String [] _diagnosis = diagnosis.split(";");
                    for (String code : _diagnosis) {
                        String chronicInfo = redisService.getChronicInfo(code);
                        if (!StringUtils.isEmpty(chronicInfo)) {
                            String [] _chronicInfo = chronicInfo.split("-");
                            if (!"0".equals(_chronicInfo[1])) {
                                String healthProblem = redisService.getHpCodeByIcd10(code); //祝金仙
                                if (!StringUtils.isEmpty(healthProblem)) {
                                    for (String hpCode : healthProblem.split(";")) {
                                        List<Map<String, Object>> profileList = new ArrayList<>();
                                        if (hpMap.containsKey(hpCode)) {
                                            profileList = hpMap.get(hpCode);
                                        }
                                        profileList.add(item);
                                        hpMap.put(hpCode, profileList);
                                    }
                                }
                            }
                        }
                    }
                }
            });
            for (String hpCode : hpMap.keySet()) {
                Map<String, Object> obj = new HashedMap();
                obj.put("healthProblemCode", hpCode);
                obj.put("healthProblemName", redisService.getHealthProblem(hpCode));
                int visitTimes = 0;
                int hospitalizationTimes = 0;
                List<Map<String, Object>> profileList = hpMap.get(hpCode);
                Map<String, List<Map<String, Object>>> complication = new HashMap<>();
                for (int i = 0; i < profileList.size(); i++) {
                    Map<String, Object> profile = profileList.get(i);
                    //事件类型
                    String eventType = (String) profile.get(ResourceCells.EVENT_TYPE);
                    String recentEvent = "";
                    if ("0".equals(eventType)) {
                        recentEvent = "门诊";
                        visitTimes ++;
                    } else if ("1".equals(eventType)) {
                        recentEvent = "住院";
                        hospitalizationTimes ++;
                    } else if ("2".equals(eventType)) {
                        recentEvent = "体检";
                    }
                    //第一条
                    if (i == 0) {
                        obj.put("lastVisitDate", profile.get(ResourceCells.EVENT_DATE));
                        obj.put("lastVisitOrgCode", profile.get(ResourceCells.ORG_CODE));
                        obj.put("lastVisitOrg", profile.get(ResourceCells.ORG_NAME));
                        obj.put("lastVisitRecord", profile.get(ResourceCells.ROWKEY));
                        obj.put("recentEvent", recentEvent);
                        obj.put("eventType", eventType);
                    }
                    //最后一条
                    if (i == profileList.size() - 1) {
                        obj.put("ageOfDisease", getAgeOfDisease(profile.get(ResourceCells.EVENT_DATE)));
                        obj.put("firstVisitDate", profile.get(ResourceCells.EVENT_DATE));
                        obj.put("firstVisitOrgCode", profile.get(ResourceCells.ORG_CODE));
                        obj.put("firstVisitOrg", profile.get(ResourceCells.ORG_NAME));
                        obj.put("firstVisitRecord", profile.get(ResourceCells.ROWKEY));
                    }
                    //提取并发症
                    if (profile.containsKey(ResourceCells.DIAGNOSIS)) {
                        String diagnosis = (String) profile.get(ResourceCells.DIAGNOSIS);
                        String [] _diagnosis = diagnosis.split(";");
                        for (String diagnosisCode : _diagnosis) {
                            String _hpCode = redisService.getHpCodeByIcd10(diagnosisCode);
                            if (_hpCode != null && !_hpCode.contains(hpCode)) {
                                if (complication.containsKey(diagnosisCode)) {
                                    complication.get(diagnosisCode).add(profile);
                                } else {
                                    List<Map<String, Object>> diagnosisList = new ArrayList<>();
                                    diagnosisList.add(profile);
                                    complication.put(diagnosisCode, diagnosisList);
                                }
                            }
                        }
                    }
                }
                //近3个月常用药物
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
                Map<String, Integer> medication = profileMedicationService.medicationRanking(demographicId, null, hpCode, date);
                obj.put("medical", medication);
                //并发症确诊
                List<Map<String, Object>> complicationList = new ArrayList<>();
                complication.forEach((key, value) -> {
                    Map<String, Object> data = new HashMap<>();
                    Collections.sort(value, new ProfileDiseaseService.EventDateComparatorDesc());
                    Map<String, Object> profile = value.get(value.size() - 1);
                    data.put("name", redisService.getIcd10Name(key));
                    data.put("date", profile.get(ResourceCells.EVENT_DATE));
                    data.put("org", profile.get(ResourceCells.ORG_NAME));
                    data.put("record", profile.get(ResourceCells.ROWKEY));
                    complicationList.add(data);
                });
                obj.put("complication", complicationList);
                obj.put("visitTimes", visitTimes);
                obj.put("hospitalizationTimes", hospitalizationTimes);
                obj.put("demographicId", demographicId);
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * 根据就诊记录，分析历史健康情况
     * @param demographicId
     * @return
     */
    public List<Map<String, Object>> getHealthCondition (String demographicId) {
        List<Map<String, Object>> result = new ArrayList<>();
        //获取门诊住院记录
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", 1, 500, null);
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        Map<String, List<Map<String, Object>>> hpMap = new HashedMap();
        if (eventList.size() > 0) {
            //进行降序
            Collections.sort(eventList, new ProfileDiseaseService.EventDateComparatorDesc());
            for (Map<String, Object> item : eventList) {
                if (item.containsKey(ResourceCells.HEALTH_PROBLEM)) {
                    String healthProblem = (String) item.get(ResourceCells.HEALTH_PROBLEM);
                    String [] _healthProblem = healthProblem.split(";");
                    for (String code : _healthProblem) {
                        List<Map<String, Object>> profileList = new ArrayList<>();
                        if (hpMap.containsKey(code)) {
                            profileList = hpMap.get(code);
                        }
                        profileList.add(item);
                        hpMap.put(code, profileList);
                    }
                }
            }
            hpMap = sortByValue(hpMap);
            for (String hpCode : hpMap.keySet()) {
                Map<String, Object> obj = new HashedMap();
                obj.put("healthProblemCode", hpCode);
                obj.put("healthProblemName", redisService.getHealthProblem(hpCode));
                int visitTimes = 0;
                int hospitalizationTimes = 0;
                int operateTimes = 0;
                Map<String, Integer> commonSymptoms = new HashMap<>();
                List<Map<String, Object>> profileList = hpMap.get(hpCode);
                for (int i = 0; i < profileList.size(); i++) {
                    Map<String, Object> profile = profileList.get(i);
                    //事件类型
                    String eventType = (String) profile.get(ResourceCells.EVENT_TYPE);
                    String recentEvent = "";
                    if ("0".equals(eventType)) {
                        recentEvent = "门诊";
                        visitTimes ++;
                    } else if ("1".equals(eventType)) {
                        recentEvent = "住院";
                        hospitalizationTimes ++;
                    } else if ("2".equals(eventType)) {
                        recentEvent = "体检";
                    }
                    String subQ = "{\"q\":\"rowkey:" + profile.get(ResourceCells.ROWKEY) + "$HDSD00_06$*" + "\"}";
                    Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                    if (subEnvelop.getDetailModelList().size() > 0) {
                        operateTimes ++;
                    }
                    //第一条
                    if (i == 0) {
                        obj.put("lastVisitDate", profile.get(ResourceCells.EVENT_DATE));
                        obj.put("lastVisitOrgCode", profile.get(ResourceCells.ORG_CODE));
                        obj.put("lastVisitOrg", profile.get(ResourceCells.ORG_NAME));
                        obj.put("lastVisitRecord", profile.get(ResourceCells.ROWKEY));
                        obj.put("recentEvent", recentEvent);
                        obj.put("eventType", eventType);
                    }
                    //最后一条
                    if (i == profileList.size() - 1) {
                        obj.put("firstVisitDate", profile.get(ResourceCells.EVENT_DATE));
                        obj.put("firstVisitOrgCode", profile.get(ResourceCells.ORG_CODE));
                        obj.put("firstVisitOrg", profile.get(ResourceCells.ORG_NAME));
                        obj.put("firstVisitRecord", profile.get(ResourceCells.ROWKEY));
                    }
                    //提取常见症状
                    if (profile.containsKey(ResourceCells.DIAGNOSIS)) {
                        String diagnosis = (String) profile.get(ResourceCells.DIAGNOSIS);
                        String [] _diagnosis = diagnosis.split(";");
                        for (String diagnosisCode : _diagnosis) {
                            String healthProblem = redisService.getHpCodeByIcd10(diagnosisCode);//通过ICD10获取健康问题
                            if (!StringUtils.isEmpty(healthProblem)) {
                                if (healthProblem.contains(hpCode)) {
                                    if (commonSymptoms.containsKey(diagnosisCode)) {
                                        int count = commonSymptoms.get(diagnosisCode);
                                        commonSymptoms.put(diagnosisCode, count++) ;
                                    } else {
                                        commonSymptoms.put(diagnosisCode, 1) ;
                                    }
                                }
                            }
                        }
                    }
                }
                //常见症状
                List<Map<String, Object>> commonSymptomsList = new ArrayList<>();
                commonSymptoms.forEach((key, value) -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", redisService.getIcd10Name(key));
                    data.put("count", value);
                    commonSymptomsList.add(data);
                });
                obj.put("commonSymptoms", commonSymptomsList);
                obj.put("visitTimes", visitTimes);
                obj.put("hospitalizationTimes", hospitalizationTimes);
                obj.put("operateTimes", operateTimes);
                obj.put("demographicId", demographicId);
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * 根据时间获取病龄
     * @param eventData
     * @return
     */
    private String getAgeOfDisease(Object eventData){
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
        String eventDataYear=eventData.toString().substring(0, 7).substring(0,4);
        String eventDataMonth=eventData.toString().substring(0, 7).substring(5,7);
        String ageOfDisease;
        if (Integer.parseInt(sd.format(new Date()).substring(4, 6)) - Integer.parseInt(eventDataMonth)<0){
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear)-1;
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))+12- Integer.parseInt(eventDataMonth);
            if (year>0) {
                ageOfDisease = year + "年" + month + "个月";
            } else {
                ageOfDisease = month + "个月";
            }
        } else {
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear);
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))- Integer.parseInt(eventDataMonth);
            if (year>0) {
                ageOfDisease = year + "年" + month + "个月";
            } else {
                ageOfDisease = month + "个月";
            }
        }
        return ageOfDisease;
    }

    static class EventDateComparatorDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String eventDate1 = (String)m1.get(ResourceCells.EVENT_DATE);
            String eventDate2 = (String)m2.get(ResourceCells.EVENT_DATE);
            String str1 = eventDate1.substring(0, eventDate1.length()-1).replaceAll("[a-zA-Z]"," ");
            String str2 = eventDate2.substring(0, eventDate1.length()-1).replaceAll("[a-zA-Z]"," ");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date v1 = null;
            Date v2 = null;
            try {
                v1 = dateFormat.parse(str1);
                v2 = dateFormat.parse(str2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }
    }

    private Map<String, List<Map<String, Object>>> sortByValue(Map<String, List<Map<String, Object>>> sourceMap) {
        if (sourceMap == null || sourceMap.isEmpty()) {
            return sourceMap;
        }
        Map<String, List<Map<String, Object>>> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<String, List<Map<String, Object>>>> entryList = new ArrayList<>(sourceMap.entrySet());
        Collections.sort(entryList, new ProfileDiseaseService.MapValueComparator());

        Iterator<Map.Entry<String, List<Map<String, Object>>>> iterator = entryList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Map<String, Object>>> tmpEntry = iterator.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    class MapValueComparator implements Comparator<Map.Entry<String, List<Map<String, Object>>>> {

        @Override
        public int compare(Map.Entry<String,  List<Map<String, Object>>> me1, Map.Entry<String,  List<Map<String, Object>>> me2) {

            return - new Integer(me1.getValue().size()).compareTo(new Integer(me2.getValue().size()));
        }
    }

}
