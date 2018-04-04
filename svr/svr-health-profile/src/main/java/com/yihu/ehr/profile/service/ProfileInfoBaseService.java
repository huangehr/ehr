package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class ProfileInfoBaseService {

    @Value("${spring.application.id}")
    private String appId;
    @Autowired
    private ResourceClient resource; //资源服务
    @Autowired
    private ProfileDiseaseService profileDiseaseService;

    /**
     * @获取患者档案基本信息
     */
    public Map<String, Object> getPatientInfo(String demographicId, String version) {
        //时间排序
        /*if (version != null) {
            envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, version);
        } else {
            envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, null);
        }*/
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", 1, 500, null);
        List<Map<String, Object>> list = envelop.getDetailModelList();
        Map<String, Object> patientMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            Map<String, Object> result = new HashMap<>();
            if (list.size() == 1) {
                result = list.get(0);
            } else {
                //合并数据
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!result.containsKey(key)) {
                            result.put(key, obj.get(key));
                        }
                    }
                }
            }
            //提取档案类型信息
            // 1. 新生儿 -> 出生医学证明编号 新生儿姓名 新生儿性别代码 新生儿出生日期
            if (result.get("EHR_001219") != null || result.get("EHR_001251") != null || result.get("EHR_001252") != null || result.get("EHR_001253") != null) {
                patientMap.put("label", "新生儿");
                //出生身高
                patientMap.put("height", result.get("EHR_001256") == null ? "" : result.get("EHR_001256"));
                //出生体重
                patientMap.put("weight", result.get("EHR_001257") == null ? "" : result.get("EHR_001257")); //单位(g)
                //2. 慢病
            } else {
                patientMap.put("label", "患病");
                //身高
                String height = "";
                if (!StringUtils.isEmpty(result.get("EHR_000590"))) {
                    height = (String) result.get("EHR_000590");
                }
                if (StringUtils.isEmpty(height) && !StringUtils.isEmpty(result.get("EHR_000778"))) {
                    height = (String) result.get("EHR_000778");
                }
                if (StringUtils.isEmpty(height) && !StringUtils.isEmpty(result.get("EHR_001339"))) {
                    height = (String) result.get("EHR_001339");
                }
                if (StringUtils.isEmpty(height) && !StringUtils.isEmpty(result.get("EHR_004263"))) {
                    height = (String) result.get("EHR_004263");
                }
                if (StringUtils.isEmpty(height) && !StringUtils.isEmpty(result.get("EHR_005142"))) {
                    height = (String) result.get("EHR_005142");
                }
                patientMap.put("height", height);
                //体重
                String weight = "";
                if (!StringUtils.isEmpty(result.get("EHR_000073"))) {
                    weight = (String) result.get("EHR_000073");
                }
                if (StringUtils.isEmpty(weight) && !StringUtils.isEmpty(result.get("EHR_000785"))) {
                    weight = (String) result.get("EHR_000785");
                }
                if (StringUtils.isEmpty(weight) && !StringUtils.isEmpty(result.get("EHR_001340"))) {
                    weight = (String) result.get("EHR_001340");
                }
                if (StringUtils.isEmpty(weight) && !StringUtils.isEmpty(result.get("EHR_004002"))) {
                    weight = (String) result.get("EHR_004002");
                }
                if (StringUtils.isEmpty(weight) && !StringUtils.isEmpty(result.get("EHR_004718"))) {
                    weight = (String) result.get("EHR_004718");
                }
                if (StringUtils.isEmpty(weight) && !StringUtils.isEmpty(result.get("EHR_005148"))) {
                    weight = (String) result.get("EHR_005148");
                }
                patientMap.put("weight", weight);
            }
            //姓名
            patientMap.put("name", result.get("patient_name") == null? "" : result.get("patient_name"));
            //性别
            String gender = result.get("EHR_000019") == null? "" : (String) result.get("EHR_000019");
            if (gender.equals("1")) {
                gender = "男";
            } else if (gender.equals("2")) {
                gender = "女";
            }
            patientMap.put("gender", gender == null ? "未知" : gender);
            //出生日期
            String birthday = "";
            if (!StringUtils.isEmpty(result.get("EHR_000007"))) {
                birthday = (String) result.get("EHR_000007");
            }
            if (StringUtils.isEmpty(birthday) && !StringUtils.isEmpty(result.get("EHR_000320"))) {
                birthday = (String) result.get("EHR_000320");
            }
            patientMap.put("birthday", birthday);
            //年龄
            if (!StringUtils.isEmpty(patientMap.get("birthday"))) {
                String date1 = patientMap.get("birthday").toString();
                String date2 = date1.substring(0, date1.indexOf("T"));
                Date date3 = DateUtil.formatCharDateYMD(date2, "yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                int now = calendar.get(Calendar.YEAR);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date3);
                int birthdayYear = calendar1.get(Calendar.YEAR);
                patientMap.put("age", String.valueOf(now - birthdayYear));
            } else {
                patientMap.put("age", "");
            }
            //民族
            patientMap.put("nation", result.get("EHR_000016_VALUE") == null ? "" : result.get("EHR_000016_VALUE"));
            //婚姻状态
            String marriage = "";
            if (!StringUtils.isEmpty(result.get("EHR_000014_VALUE"))) {
                marriage = (String) result.get("EHR_000014_VALUE");
            }
            if (StringUtils.isEmpty(marriage) && !StringUtils.isEmpty(result.get("EHR_004984_VALUE"))) {
                marriage = (String) result.get("EHR_004984_VALUE");
            }
            patientMap.put("marriage", marriage);
            //婚育
            patientMap.put("fertility", result.get("EHR_000540") == null ? "" : result.get("EHR_000540"));
            //ABO血型
            patientMap.put("ABO", result.get("EHR_000001") == null ? "" : result.get("EHR_000001"));
            //Rh血型
            patientMap.put("Rh", result.get("EHR_000002") == null ? "" : result.get("EHR_000002"));
            //过敏药物
            patientMap.put("allergyMedicine", result.get("EHR_004971") == null ? "" : result.get("EHR_004971"));
            //过敏源
            patientMap.put("allergens", result.get("EHR_000011") == null ? "" : result.get("EHR_000011"));
            //电话
            patientMap.put("patientTel", result.get("EHR_000003") == null ? "" : result.get("EHR_000003"));
            //国籍
            patientMap.put("country", result.get("EHR_004970_VALUE") == null ? "" : result.get("EHR_004970_VALUE"));
            //工作单位
            patientMap.put("employer", result.get("EHR_002930") == null ? "" : result.get("EHR_002930"));
            //户籍
            if (result.get("EHR_004945") != null && result.get("EHR_004946") != null) {
                patientMap.put("householdRegister", result.get("EHR_004945") + "-" + result.get("EHR_004946"));
            } else if (result.get("EHR_004945") != null && result.get("EHR_004946") == null) {
                patientMap.put("householdRegister", result.get("EHR_004945"));
            } else if (result.get("EHR_004945") == null && result.get("EHR_004946") != null) {
                patientMap.put("householdRegister", result.get("EHR_004946"));
            } else {
                patientMap.put("householdRegister", "");
            }
            patientMap.put("img", "");
            return patientMap;
        } else {
            return patientMap;
        }
    }

    /**
     * 既往史 - pc档案浏览器
     * @param demographic_id
     * @return
     */
    public List<Map<String, Object>> profileHistory(String demographic_id) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //疾病史
        List<Map<String, Object>> list1 =  profileDiseaseService.getHealthProblem(demographic_id);
        if (list1 != null && list1.size() > 0) {
            Map<String, Object> medicalHistory = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder();
            for (Map temp : list1) {
                String healthProblemName = (String) temp.get("healthProblemName");
                stringBuilder.append(healthProblemName);
                stringBuilder.append("、");
            }
            medicalHistory.put("label", "疾病史");
            medicalHistory.put("info", stringBuilder.toString());
            resultList.add(medicalHistory);
        }
        Envelop envelop;
        //传染病史
        String q2 = "{\"q\":\"demographic_id:" + demographic_id + " AND EHR_002393:*\"}";
        envelop = resource.getMasterData(q2, 1, 500, null);
        List<Map<String, Object>> list2 = envelop.getDetailModelList();
        if (list2 != null && list2.size() > 0) {
            Map<String, Object> infectiousDisease = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder();
            for (Map temp : list2) {
                String name = (String) temp.get("EHR_002386");
                stringBuilder.append(name);
                stringBuilder.append("、");
            }
            infectiousDisease.put("label", "传染病史");
            infectiousDisease.put("info", stringBuilder.toString());
            resultList.add(infectiousDisease);
        }
        //预防接种史
        String q3 = "{\"q\":\"demographic_id:" + demographic_id + " AND EHR_002443:*\"}";
        envelop = resource.getMasterData(q3, 1, 500, null);
        List<Map<String, Object>> list3 = envelop.getDetailModelList();
        if (list3 != null && list3.size() > 0) {
            Map<String, Object> vaccination = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder();
            for (Map temp : list3) {
                String name = (String) temp.get("EHR_002449");
                stringBuilder.append(name);
                stringBuilder.append("、");
            }
            vaccination.put("label", "预防接种史");
            vaccination.put("info", stringBuilder.toString());
            resultList.add(vaccination);
        }
        //手术史
        String q4 = "{\"q\":\"demographic_id:" + demographic_id + "\"}";
        envelop = resource.getMasterData(q4, 1, 500, null);
        List<Map<String, Object>> list4 = envelop.getDetailModelList();
        if (list4 != null && list4.size() > 0) {
            Map<String, Object> surgery = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder();
            int index = 1;
            for (Map temp : list4) {
                String masterRowKey = (String) temp.get("rowkey");
                String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSD00_06$*" + "\"}";
                Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                if (subList != null && subList.size() > 0) {
                    for (Map subTemp : subList) {
                        String operateName;
                        if (temp.get("EHR_000418") != null) {
                            operateName = (String) subTemp.get("EHR_000418");
                        } else {
                            operateName = (String) subTemp.get("EHR_004045");
                        }
                        stringBuilder.append(operateName);
                        stringBuilder.append("、");
                        index ++;
                        break;
                    }
                }
            }
            if (index > 1) {
                surgery.put("label", "手术史");
                surgery.put("info", stringBuilder.toString());
                resultList.add(surgery);
            }
        }
        //孕产史
        String q5 = "{\"q\":\"demographic_id:" + demographic_id + " AND EHR_002443:*\"}";
        envelop = resource.getMasterData(q5, 1, 500, null);
        List<Map<String, Object>> list5 = envelop.getDetailModelList();
        if (list5 != null && list5.size() > 0) {
            Map<String, Object> childbirth = new HashMap<>(2);
            StringBuilder stringBuilder = new StringBuilder();
            for (Map temp : list5) {
                String end = (String) temp.get("EHR_001654");
                stringBuilder.append(end);
                stringBuilder.append("、");
            }
            childbirth.put("label", "孕产史");
            childbirth.put("info", stringBuilder.toString());
            resultList.add(childbirth);
        }
        return resultList;
    }

    /**
     * 既往史 - mobile居民端
     * @param demographic_id
     * @return
     */
    public List<Map<String, Object>> pastHistory(String demographic_id) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //药物过敏
        Map<String, Object> patientInfo = getPatientInfo(demographic_id, null);
        Map<String, Object> allergyMedicine = new HashMap<>();
        allergyMedicine.put("label", "药物过敏");
        allergyMedicine.put("info", patientInfo.get("allergyMedicine"));
        resultList.add(allergyMedicine);

        //疾病
        Map<String, Object> medicalHistory = new HashMap<>();
        List<Map<String, Object>> list1 =  profileDiseaseService.getHealthProblem(demographic_id);
        StringBuilder stringBuilder1 = new StringBuilder();
        if (list1 != null && list1.size() > 0) {
            for (Map temp : list1) {
                String healthProblemName = (String) temp.get("healthProblemName");
                stringBuilder1.append(healthProblemName);
                stringBuilder1.append("、");
            }
        }
        medicalHistory.put("label", "疾病");
        medicalHistory.put("info", stringBuilder1.toString());
        resultList.add(medicalHistory);

        //手术
        Envelop envelop;
        Map<String, Object> surgery = new HashMap<>();
        String q4 = "{\"q\":\"demographic_id:" + demographic_id + "\"}";
        envelop = resource.getMasterData(q4, 1, 500, null);
        List<Map<String, Object>> list4 = envelop.getDetailModelList();
        StringBuilder stringBuilder4 = new StringBuilder();
        if (list4 != null && list4.size() > 0) {
            for (Map temp : list4) {
                String masterRowKey = (String) temp.get("rowkey");
                String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSD00_06$*" + "\"}";
                envelop = resource.getSubData(subQ, null, null, null);
                List<Map<String, Object>> subList = envelop.getDetailModelList();
                if (subList != null && subList.size() > 0) {
                    for (Map subTemp : subList) {
                        String operateName;
                        if (temp.get("EHR_000418") != null) {
                            operateName = (String) subTemp.get("EHR_000418");
                        } else {
                            operateName = (String) subTemp.get("EHR_004045");
                        }
                        stringBuilder4.append(operateName);
                        stringBuilder4.append("、");
                        break;
                    }
                }
            }
        }
        surgery.put("label", "手术");
        surgery.put("info", stringBuilder4.toString());
        resultList.add(surgery);

        //外伤
        Map<String, Object> trauma = new HashMap<>();
        trauma.put("label", "外伤");
        trauma.put("info", "");
        resultList.add(trauma);

        //输血
        Map<String, Object> bloodTransfusion = new HashMap<>();
        bloodTransfusion.put("label", "输血");
        bloodTransfusion.put("info", "");
        resultList.add(bloodTransfusion);

        //预防接种
        Map<String, Object> vaccination = new HashMap<>(2);
        String q3 = "{\"q\":\"demographic_id:" + demographic_id + " AND EHR_002443:*\"}";
        envelop = resource.getMasterData(q3, 1, 500, null);
        List<Map<String, Object>> list3 = envelop.getDetailModelList();
        StringBuilder stringBuilder3 = new StringBuilder();
        if (list3 != null && list3.size() > 0) {
            for (Map temp : list3) {
                String name = (String) temp.get("EHR_002449");
                stringBuilder3.append(name);
                stringBuilder3.append("、");
            }
        }
        vaccination.put("label", "预防接种");
        vaccination.put("info", stringBuilder3.toString());
        resultList.add(vaccination);
        return resultList;
    }

    /**
     * 过敏史
     * @param demographic_id
     * @return
     */
    public Map<String, Object> allergensHistory(String demographic_id) {
        Map<String, Object> patientInfo = getPatientInfo(demographic_id, null);
        Map<String, Object> allergens = new HashMap<>();
        allergens.put("label", "过敏源");
        allergens.put("info", patientInfo.get("allergens"));
        return allergens;
    }

    /**
     * 家族史
     * @param demographic_id
     * @return
     */
    public Map<String, Object> familyHistory(String demographic_id) {
        Map<String, Object> familyHistory = new HashMap<>();
        familyHistory.put("label", "家族史");
        familyHistory.put("info", "");
        return familyHistory;
    }

    /**
     * 个人史
     * @param demographic_id
     * @return
     */
    public Map<String, Object> personHistory(String demographic_id) {
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographic_id + "\"}", 1, 500, null);
        List<Map<String, Object>> list = envelop.getDetailModelList();
        Map<String, Object> personHistory = new HashMap<>();
        if (list != null && list.size() > 0) {
            Map<String, Object> result = new HashMap<>();
            if (list.size() == 1) {
                result = list.get(0);
            } else {
                //合并数据
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!result.containsKey(key)) {
                            result.put(key, obj.get(key));
                        }
                    }
                }
            }
            personHistory.put("name", result.get("patient_name") == null? "" : result.get("patient_name"));
            //出生地
            String placeOfBirth = "";
            String province = (String) result.get("EHR_004945");
            String city = (String) result.get("EHR_004946") ;
            String county = (String) result.get("EHR_004947");
            if (!StringUtils.isEmpty(province) && !province.equals("-")) {
                placeOfBirth += province + "-";
            }
            if (!StringUtils.isEmpty(city) && !city.equals("-")) {
                placeOfBirth += city + "-";
            }
            if (!StringUtils.isEmpty(county) && !county.equals("-")) {
                placeOfBirth += county;
            }
            personHistory.put("placeOfBirth", placeOfBirth);
            //居住地
            String placeOfResidence = "";
            if (!StringUtils.isEmpty(result.get("EHR_000760"))) {
                placeOfResidence = (String) result.get("EHR_000760");
            }
            if (StringUtils.isEmpty(placeOfResidence) && !StringUtils.isEmpty(result.get("EHR_003051"))) {
                placeOfResidence = (String) result.get("EHR_003051");
            }
            personHistory.put("placeOfResidence", placeOfResidence);
            //生活条件
            personHistory.put("livingCondition", "");
            //文化程度
            personHistory.put("educationLevel", result.get("EHR_000790") == null? "" : result.get("EHR_000790"));
            //职业
            personHistory.put("career", result.get("EHR_000022_VALUE") == null? "" : result.get("EHR_000022_VALUE"));
            //嗜烟
            String smoke = "";
            if (!StringUtils.isEmpty(result.get("EHR_002114"))) {
                smoke = (String) result.get("EHR_002114");
            }
            if (StringUtils.isEmpty(smoke) && !StringUtils.isEmpty(result.get("EHR_004722"))) {
                smoke = (String) result.get("EHR_004722");
            }
            personHistory.put("smoke", smoke);
            //嗜酒
            String alcohol = "";
            if (!StringUtils.isEmpty(result.get("EHR_002117"))) {
                alcohol = (String) result.get("EHR_002117");
            }
            if (StringUtils.isEmpty(alcohol) && !StringUtils.isEmpty(result.get("EHR_004728"))) {
                alcohol = (String) result.get("EHR_004728");
            }
            personHistory.put("alcohol", alcohol);
            //疫水接触
            personHistory.put("epidemicWater contact", "");
            //疫区接触
            personHistory.put("infectedArea", "");
            //放射性物质接触
            personHistory.put("radioactiveMaterialContact", "");
        }
        return personHistory;
    }

    private int CompareAgeOfDisease(String AgeOfDisease1, String AgeOfDisease2){
        int year1 = 0;
        int month1 = 0;
        int year2 = 0;
        int month2 = 0;
        if (AgeOfDisease1.split("年|个月").length>1) {
            year1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
            month1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[1]);
        } else {
            month1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
        }
        if (AgeOfDisease2.split("年|个月").length>1) {
            year2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
            month2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[1]);
        } else {
            month2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
        }
        if (year1 * 12 + month1 <= year2 * 12 + month2) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 全文检索
     */
    public Envelop getProfileLucene(String startTime, String endTime,List<String> lucene, Integer page, Integer size) throws Exception {
        String queryParams = "";
        if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
            queryParams = BasisConstant.eventDate+":["+startTime+" TO "+endTime+"]";
        } else {
            if (startTime!=null && startTime.length()>0) {
                queryParams = BasisConstant.eventDate+":["+startTime+" TO *]";
            } else if (endTime!=null && endTime.length()>0){
                queryParams = BasisConstant.eventDate+":[* TO "+endTime+"]";
            }
        }
        //全文检索
        Envelop re = resource.getResources(BasisConstant.patientEvent, "*", "*", "{\"q\":\""+queryParams.replace(' ','+')+"\"}", page, size);
        return re;
    }
}
