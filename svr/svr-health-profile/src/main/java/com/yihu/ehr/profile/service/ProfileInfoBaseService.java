package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.http.HttpResponse;
import com.yihu.ehr.util.http.HttpUtils;
import com.yihu.ehr.util.rest.Envelop;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class ProfileInfoBaseService extends BaseJpaService {

    @Autowired
    private ResourceClient resource;
    @Autowired
    private ProfileDiseaseService profileDiseaseService;
    @Autowired
    protected RedisService redisService;


    /**
     * @获取患者档案基本信息
     */
    public Map<String, Object> getPatientInfo(String demographicId, String version) throws Exception {
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", 1, 1000, null);
        List<Map<String, Object>> list = envelop.getDetailModelList();
        Map<String, Object> patientMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            Map<String, Object> result = new HashMap<>();
            List<String> allergyMedicine = new ArrayList<>();
            List<String> allergens = new ArrayList<>();
            if (list.size() == 1) {
                result = list.get(0);
                if (result.containsKey("EHR_004971")) {
                    allergyMedicine.add((String) result.get("result"));
                }
                if (result.containsKey("EHR_000011")) {
                    allergens.add((String) result.get("EHR_000011"));
                }
            } else {
                //合并数据
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if ("EHR_004971".equals(key)) {
                            if (!allergyMedicine.contains(obj.get("EHR_004971"))) {
                                allergyMedicine.add((String) obj.get("EHR_004971"));
                            }
                            continue;
                        }
                        if ("EHR_000011".equals(key)) {
                            if (!allergens.contains(obj.get("EHR_000011"))) {
                                allergens.add((String) obj.get("EHR_000011"));
                            }
                            continue;
                        }
                        if (!result.containsKey(key) && obj.get(key) != null) {
                            result.put(key, obj.get(key));
                        }
                    }
                }
            }
            //提取档案类型信息
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
            List<String> labels = new ArrayList<>();
            if (result.get(ResourceCells.DIAGNOSIS) != null && result.get(ResourceCells.DIAGNOSIS).toString().contains("Z37")) { //1 新生儿
                labels.add("新生儿");
                //出生身高
                patientMap.put("height", result.get("EHR_001256") == null ? "" : result.get("EHR_001256"));
                //出生体重
                patientMap.put("weight", result.get("EHR_001257") == null ? "" : result.get("EHR_001257")); //单位(g)
            } else if (result.get(ResourceCells.DIAGNOSIS) != null && result.get(ResourceCells.DIAGNOSIS).toString().contains("O80")) { //2 孕妇
                labels.add("孕妇");
            } else if (profileDiseaseService.getHealthProblem(demographicId).size() > 0){
                labels.add("慢病");
            }
            Map<String, Object> params = new HashMap<>();
            params.put("idcard", demographicId);
            try {
                HttpResponse httpResponse = HttpUtils.doGet("http://localhost:8080/label/searchehrbaseinfo", params);
                if (httpResponse.isSuccessFlg()) {
                    Map<String, Object> labelResult = objectMapper.readValue(httpResponse.getContent(), Map.class);
                    labels = (List) labelResult.get("label");
                }
            } catch (Exception e) {

            }
            patientMap.put("label", labels);
            //姓名
            patientMap.put("name", result.get("patient_name") == null ? "" : result.get("patient_name"));
            //性别
            String gender = result.get("EHR_000019") == null ? "" : (String) result.get("EHR_000019");
            if ("1".equals(gender)) {
                patientMap.put("gender", "男");
            } else if ("2".equals(gender)) {
                patientMap.put("gender", "女");
            } else {
                patientMap.put("gender", "未知");
            }
            //出生日期
            String birthday = "";
            if (StringUtils.isEmpty(birthday) && !StringUtils.isEmpty(result.get("EHR_000007"))) {
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
            patientMap.put("ABO", result.get("EHR_000001_VALUE") == null ? "" : result.get("EHR_000001_VALUE"));
            //Rh血型
            patientMap.put("Rh", result.get("EHR_000002_VALUE") == null ? "" : result.get("EHR_000002_VALUE"));
            //过敏药物
            patientMap.put("allergyMedicine", allergyMedicine.isEmpty() ? "" : org.apache.commons.lang3.StringUtils.join(allergyMedicine, "、"));
            //过敏源
            patientMap.put("allergens", allergens.isEmpty() ? "" : org.apache.commons.lang3.StringUtils.join(allergens, "、"));
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
            patientMap.put("imgRemotePath", imgRemotePath(demographicId));
            return patientMap;
        } else {
            return patientMap;
        }
    }

    /**
     * 既往史
     * @param demographic_id
     * @return
     */
    public List<Map<String, Object>> pastHistory(String demographic_id) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //药物过敏
        Map<String, Object> patientInfo = getPatientInfo(demographic_id, null);
        Map<String, Object> allergyMedicine = new HashMap<>();
        allergyMedicine.put("label", "药物过敏");
        allergyMedicine.put("info", patientInfo.get("allergyMedicine"));
        resultList.add(allergyMedicine);

        //疾病 & 手术
        Envelop envelop;
        Map<String, Object> medicalHistory = new HashMap<>();
        Map<String, Object> surgery = new HashMap<>();
        String q4 = "{\"q\":\"demographic_id:" + demographic_id + "\"}";
        envelop = resource.getMasterData(q4, 1, 1000, null);
        List<Map<String, Object>> list4 = envelop.getDetailModelList();
        Set<String> hpSet = new HashSet<>();
        StringBuilder stringBuilder4 = new StringBuilder();
        if (list4 != null && list4.size() > 0) {
            for (Map temp : list4) {
                if (!StringUtils.isEmpty(temp.get(ResourceCells.HEALTH_PROBLEM_NAME))) {
                    String [] hpNames = temp.get(ResourceCells.HEALTH_PROBLEM_NAME).toString().split(";");
                    for (String hpName : hpNames) {
                        hpSet.add(hpName);
                    }
                } else if (!StringUtils.isEmpty(temp.get(ResourceCells.HEALTH_PROBLEM))) {
                    String [] _hpCode = ((String) temp.get(ResourceCells.HEALTH_PROBLEM)).split(";");
                    for (String code : _hpCode) {
                        String name = redisService.getHealthProblem(code);
                        if (!StringUtils.isEmpty(name)) {
                            hpSet.add(name);
                        }
                    }
                }
                String masterRowKey = (String) temp.get("rowkey");
                String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSD00_06$*" + "\"}";
                envelop = resource.getSubData(subQ, 1, 1000, null);
                List<Map<String, Object>> subList = envelop.getDetailModelList();
                if (subList != null && subList.size() > 0) {
                    for (Map subTemp : subList) {
                        if (subTemp.get("EHR_000418") != null) {
                            String operateName = (String) subTemp.get("EHR_000418");
                            stringBuilder4.append(operateName);
                            stringBuilder4.append("、");
                            break;
                        }
                    }
                }
            }
        }
        medicalHistory.put("label", "疾病");
        medicalHistory.put("info", org.apache.commons.lang3.StringUtils.join(hpSet, "、"));
        resultList.add(medicalHistory);
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
        String q5 = "{\"q\":\"demographic_id:" + demographic_id + "\"}";
        envelop = resource.getMasterData(q5, 1, 1000, null);
        List<Map<String, Object>> list5 = envelop.getDetailModelList();
        StringBuilder stringBuilder5 = new StringBuilder();
        if (list5 != null && list5.size() > 0) {
            for (Map temp : list5) {
                String masterRowKey = (String) temp.get("rowkey");
                String subQ = "{\"q\":\"rowkey:" + masterRowKey + "$HDSB03_12$*" + "\"}";
                envelop = resource.getSubData(subQ, 1, 1000, null);
                List<Map<String, Object>> subList = envelop.getDetailModelList();
                if (subList != null && subList.size() > 0) {
                    for (Map subTemp : subList) {
                        if (subTemp.get("EHR_002449") != null) {
                            String operateName = (String) subTemp.get("EHR_002449");
                            stringBuilder5.append(operateName);
                            stringBuilder5.append("、");
                            break;
                        }
                    }
                }
            }
        }
        vaccination.put("label", "预防接种");
        vaccination.put("info", stringBuilder5.toString());
        resultList.add(vaccination);
        return resultList;
    }

    /**
     * 过敏史
     * @param demographic_id
     * @return
     */
    public Map<String, Object> allergensHistory(String demographic_id) throws Exception {
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
        Envelop envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographic_id + "\"}", 1, 1000, null);
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

    private String imgRemotePath(String idCardNo) {
        String sql = "SELECT img_remote_path FROM users WHERE id_card_no = :idCardNo";
        Session session = currentSession();
        Query query = session.createSQLQuery(sql);
        query.setString("idCardNo", idCardNo);
        query.setFlushMode(FlushMode.COMMIT);
        Object path = query.uniqueResult();
        if (path != null) {
            return (String) path;
        }
        return "";
    }

    private int compareAgeOfDisease(String AgeOfDisease1, String AgeOfDisease2){
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
}
