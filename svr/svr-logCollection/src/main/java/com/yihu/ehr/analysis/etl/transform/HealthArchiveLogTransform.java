package com.yihu.ehr.analysis.etl.transform;

import com.yihu.ehr.analysis.etl.ETLConstantData;
import com.yihu.ehr.analysis.entity.UserPortrait;
import com.yihu.ehr.analysis.etl.BusinessTypeEnum;
import com.yihu.ehr.analysis.etl.ILogTransform;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 健康档案日志分析
 * <p>
 * Created by lyr-pc on 2017/2/22.
 */
public class HealthArchiveLogTransform implements ILogTransform {

    private final BusinessTypeEnum logType = BusinessTypeEnum.archive;

    @Override
    public List<UserPortrait> transform(JSONObject log) throws Exception {
        JSONObject logData = log.getJSONObject("data");
        JSONObject businessData = logData.getJSONObject("data");
        List<UserPortrait> labelInfoList = new ArrayList<>();

        if (businessData != null) {
            // 门诊记录
            if (businessData.has("event1") && businessData.getJSONArray("event1").length() > 0) {
                try {
                    JSONArray event1 = businessData.getJSONArray("event1");
                    labelInfoList.addAll(transformDiagnosis(event1, logData.getString("patient")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 住院记录
            if (businessData.has("event2") && businessData.getJSONArray("event2").length() > 0) {
                try {
                    JSONArray event2 = businessData.getJSONArray("event2");
                    labelInfoList.addAll(transformDiagnosis(event2, logData.getString("patient")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 用药记录
            if (businessData.has("event3")) {
                try {
                    JSONObject event3 = businessData.getJSONObject("event3");
                    labelInfoList.addAll(transformMedication(event3, logData.getString("patient")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 体检记录
            if (businessData.has("event4")) {
                try {
                    JSONObject event4 = businessData.getJSONObject("event4");
                    labelInfoList.addAll(transformPhysicalExamination(event4, logData.getString("patient")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return labelInfoList;
    }

    @Override
    public int getLogType() {
        return logType.ordinal();
    }

    @Override
    public String getLogTypeName() {
        return logType.name();
    }

    /**
     * 获取诊断结果
     *
     * @param event
     * @return
     */
    public List<UserPortrait> transformDiagnosis(JSONArray event, String patient) {
        List<UserPortrait> portraits = new ArrayList<>();

        for (int i = 0; i < event.length(); i++) {
            JSONObject log = event.getJSONObject(i);
            String diagnosis = log.getString("dianosis");

            if (StringUtils.isEmpty(diagnosis)) {
                return null;
            }

            UserPortrait userPortrait = new UserPortrait();

            userPortrait.setUserCode(patient);
            userPortrait.setCategory("1002");
            userPortrait.setSubCategory("1002001");
            userPortrait.setValue(diagnosis);
            userPortrait.setCzrq(new Date());

            portraits.add(userPortrait);
        }

        return portraits;
    }

    /**
     * 用药记录分析
     *
     * @param event
     * @return
     */
    public List<UserPortrait> transformMedication(JSONObject event, String patient) {
        List<UserPortrait> portraits = new ArrayList<>();
        if (event.has("EhrList") && event.getJSONArray("EhrList").length() > 0) {
            JSONArray ehrList = event.getJSONArray("EhrList");

            for (int i = 0; i < ehrList.length(); i++) {
                JSONObject ehr = ehrList.getJSONObject(i);
                JSONArray medicines = ehr.has("MEDICINE") ? ehr.getJSONArray("MEDICINE") : new JSONArray();

                for (int j = 0; j < medicines.length(); j++) {
                    JSONObject medicine = medicines.getJSONObject(i);

                    if (medicine.has("MEDICINE_NAME") && !StringUtils.isEmpty(medicine.getString("MEDICINE_NAME"))) {
                        UserPortrait userPortrait = new UserPortrait();

                        userPortrait.setUserCode(patient);
                        userPortrait.setCategory("1002");
                        userPortrait.setSubCategory("1002005");
                        userPortrait.setValue(medicine.getString("MEDICINE_NAME"));
                        userPortrait.setCzrq(new Date());

                        portraits.add(userPortrait);
                    }
                }
            }
        }

        return portraits;
    }

    /**
     * 体检记录分析
     *
     * @param event
     * @param patient
     * @return
     */
    public List<UserPortrait> transformPhysicalExamination(JSONObject event, String patient) {
        List<UserPortrait> portraits = new ArrayList<>();

        // 体质指数
        if (event.has("bmi") && StringUtils.isNotEmpty(event.getString("bmi"))) {
            UserPortrait userPortrait = new UserPortrait();

            userPortrait.setUserCode(patient);
            userPortrait.setCategory("1002");
            userPortrait.setSubCategory("1002004");
            userPortrait.setValue(ETLConstantData.bmiName(Double.parseDouble(event.getString("bmi"))));
            userPortrait.setCzrq(new Date());

            portraits.add(userPortrait);
        }

        // 空腹血糖
        if (event.has("fastingPlasmaGlucoseL") && StringUtils.isNotEmpty(event.getString("fastingPlasmaGlucoseL"))) {
            String xueTang = event.getString("fastingPlasmaGlucoseL");
            int v = ETLConstantData.xueTangBefore(Double.parseDouble(xueTang));

            if (v == 1) {
                UserPortrait userPortrait = new UserPortrait();

                userPortrait.setUserCode(patient);
                userPortrait.setCategory("1002");
                userPortrait.setSubCategory("1002004");
                userPortrait.setValue("血糖偏高");
                userPortrait.setCzrq(new Date());

                portraits.add(userPortrait);
            } else if (v == -1) {
                UserPortrait userPortrait = new UserPortrait();

                userPortrait.setUserCode(patient);
                userPortrait.setCategory("1002");
                userPortrait.setSubCategory("1002004");
                userPortrait.setValue("血糖偏低");
                userPortrait.setCzrq(new Date());

                portraits.add(userPortrait);
            }
        }

        // 心率
        if (event.has("heartRate") && StringUtils.isNotEmpty(event.getString("heartRate"))) {
            String rate = event.getString("heartRate");
            UserPortrait userPortrait = new UserPortrait();

            userPortrait.setUserCode(patient);
            userPortrait.setCategory("1002");
            userPortrait.setSubCategory("1002006");
            userPortrait.setValue(ETLConstantData.heartRate(Double.parseDouble(rate)));
            userPortrait.setCzrq(new Date());

            portraits.add(userPortrait);
        }

        // 吸烟情况
        if (event.has("dailySmokingQuantity") && StringUtils.isNotEmpty(event.getString("dailySmokingQuantity"))) {
            String cy = event.getString("dailySmokingQuantity");
            if (cy.compareTo("0") > 0) {
                UserPortrait userPortrait = new UserPortrait();

                userPortrait.setUserCode(patient);
                userPortrait.setCategory("1003");
                userPortrait.setSubCategory("1003001");
                userPortrait.setValue("吸烟");
                userPortrait.setCzrq(new Date());

                portraits.add(userPortrait);
            }
        }

        // 饮酒情况
        if (event.has("everyAlcohol_tolerance") && StringUtils.isNotEmpty(event.getString("everyAlcohol_tolerance"))) {
            String hj = event.getString("everyAlcohol_tolerance");
            if (hj.compareTo("0") > 0) {
                UserPortrait userPortrait = new UserPortrait();

                userPortrait.setUserCode(patient);
                userPortrait.setCategory("1003");
                userPortrait.setSubCategory("1003002");
                userPortrait.setValue("喝酒");
                userPortrait.setCzrq(new Date());

                portraits.add(userPortrait);
            }
        }

        // 视网膜情况
        if ((event.has("retinalHemorrhage") && StringUtils.isNotEmpty(event.getString("retinalHemorrhage"))) ||
                (event.has("papilledema") && StringUtils.isNotEmpty(event.getString("papilledema")))) {
            String retinalHemorrhage = event.getString("papilledema");
            String papilledema = event.getString("papilledema");
            if (retinalHemorrhage.equals("1") || papilledema.equals("1")) {
                UserPortrait userPortrait = new UserPortrait();

                userPortrait.setUserCode(patient);
                userPortrait.setCategory("1002");
                userPortrait.setSubCategory("1002001");
                userPortrait.setValue("视网膜病变");
                userPortrait.setCzrq(new Date());

                portraits.add(userPortrait);
            }
        }

        return portraits;
    }


    public static void main(String[] args) throws Exception {
        HealthArchiveLogTransform transform = new HealthArchiveLogTransform();
        List<UserPortrait> portraits = transform.transform(new JSONObject("{\"logType\":2,\"caller\":\"\",\"data\":{\"data\":{\"event1\":[{\"orgName\":\"厦门市海沧区海沧街道嵩屿社区卫生服务中心\",\"createTime\":\"2017-03-01 10:14:47\",\"patient\":\"915d52a7-5b1d-11e6-8344-fa163e8aee56\",\"dataFrom\":\"1\",\"id\":\"4715b0ac-7020-45f6-af51-681c29603cf3\",\"eventType\":\"1\",\"dianosis\":\"糖尿病\",\"eventDate\":\"2017-03-01 10:14:47\"}],\"event4\":{\"skinOthers\":\"\",\"renalOthers\":\"\",\"fastingPlasmaGlucoseDL\":\"\",\"abdoShiftingDull\":\"1\",\"eyegroundException\":\"\",\"abdoHepatomegalyOth\":\"\",\"electrocardiogram\":\"2\",\"typeBUltrasonic\":\"1\",\"height\":\"156.40\",\"abdoMassesOthers\":\"\",\"liverFunctionTotalBilirubin\":\"12.9\",\"bloodFatHdlc\":\"0.95\",\"heartChf\":\"\",\"renalDn\":\"\",\"bloodRoutineOthers\":\"\",\"eyeDiseaseCataract\":\"\",\"vulvaException\":\"\",\"poisonOthers\":\"\",\"cardiacSouffle\":\"1\",\"heartPrecordialpain\":\"\",\"isDryOut\":\"\",\"poisonPhysicalfactor\":\"\",\"healthGuidanceOther\":\"1\",\"hazardLoseWeight\":\"\",\"poisonDustPreStr\":\"\",\"breastOthersStr\":\"\",\"angiosisUndiscovered\":\"1\",\"bloodPressureLeftD\":\"\",\"inhospitalList\":[],\"vulva\":\"\",\"renalFunctionBun\":\"4.4\",\"cvdSah\":\"\",\"sclera\":\"正常\",\"ket\":\"-\",\"renalFunctionCreatinine\":\"50\",\"audition\":\"听见\",\"miniMentalStateExamination\":\"\",\"drinkOthers\":\"\",\"hardeningFrequency\":\"不锻炼\",\"occupationalDiseaseWorkTime\":\"\",\"asymptomatic\":\"\",\"abdoHepatomegaly\":\"1\",\"fastingPlasmaGlucoseL\":\"6.27\",\"visionRightEye\":\"4.0\",\"symptomBreastBursting\":\"\",\"poisonChemicalPreStr\":\"\",\"eatHobbySugar\":\"\",\"lips\":\"1\",\"vaginaException\":\"\",\"everyHardeningTime\":\"\",\"upcr\":\"0.746\",\"angiosisOthers\":\"\",\"occupationalDisease\":\"1\",\"motorFunction\":\"可顺利完成\",\"corporeityQiDepression\":\"\",\"heartOthers\":\"\",\"cvdOthers\":\"\",\"poisonChemical\":\"\",\"lungBreathSoundExcep\":\"\",\"angiosisDa\":\"\",\"corpus\":\"\",\"cervicalPapSmears\":\"\",\"breastOthers\":\"\",\"elderlyAffectiveState\":\"1\",\"cvdIschemicStroke\":\"\",\"lymphNodeRrmprt\":\"\",\"isExaminationExcep\":\"1\",\"symptomPalpitation\":\"\",\"heartOthersStr\":\"\",\"hardeningMode\":\"\",\"cxr\":\"\",\"lymphNodeNotTouch\":\"1\",\"lymphNodeClavicle\":\"\",\"elderlyHealthStatus\":\"基本满意\",\"othersSystemDisease\":\"2\",\"symptomDazzle\":\"\",\"symptomJointGall\":\"\",\"lungBarrelChest\":\"1\",\"symptomChestPain\":\"\",\"abdoPressPain\":\"1\",\"symptomWeightLoss\":\"\",\"occDiseaseTrades\":\"\",\"fundamentFingerpOth\":\"\",\"weight\":\"57.90\",\"symptomUrinaryUrgency\":\"\",\"drinkFrequency\":\"从不\",\"poisonOthersPreStr\":\"\",\"dentitiondentureNormal\":\"\",\"glycolatedHemoglobin\":\"7.7\",\"bloodFatLdlc\":\"2.27\",\"hazardHealthDrink\":\"\",\"othersSystemDiseaseStr\":\"高血压、糖尿病\",\"poisonOthersIspre\":\"\",\"dorsumOfFootArteriopalmus\":\"触及双侧对称\",\"renalFunctionNatremia\":\"\",\"breastMastectomy\":\"\",\"proteinuria\":\"-\",\"symptomDysuria\":\"\",\"corporeityTeBing\":\"\",\"bloodFatTriglyceride\":\"1.35\",\"urinaryOccultBlood\":\"-\",\"doctorName\":\"刘珍\",\"symptomDiarrhea\":\"\",\"poisonRadiogenPreStr\":\"\",\"abdoSplenomegalyOth\":\"\",\"breastAbnormalLactation\":\"\",\"fpList\":[],\"dentitiondentureDentalCaries\":\"\",\"symptomChronicCough\":\"\",\"symptomOther\":\"\",\"stoolOccultBlood\":\"\",\"renalFailure\":\"\",\"symptomDizziness\":\"\",\"respiratoryRate\":\"18\",\"lungRhonchusException\":\"\",\"beginSmokingAge\":\"\",\"abdoMasses\":\"1\",\"eatMeatdietFlash\":\"\",\"poisonPhysicalPreStr\":\"\",\"renalFunctionBloodPotassium\":\"\",\"platelet\":\"192\",\"liverFunctionSgot\":\"30.9\",\"uricAcid\":\"243\",\"renalCgn\":\"\",\"assistantInvesOth\":\"尿肌酐3540μmol/L,微量白蛋白/尿肌酐56.5mg/mmol\",\"examinationList\":[\"高血压\",\"糖尿病；\",\"糖化偏高；尿微量白蛋白偏高\",\"心脏起搏器；尿检异常\"],\"niVaccinationList\":[],\"symptomHandFootNumbness\":\"\",\"papilledema\":\"\",\"eatHobbyOil\":\"\",\"symptomTinnitus\":\"\",\"abdoSplenomegaly\":\"1\",\"retinalHemorrhage\":\"\",\"corporeityYangDeficiency\":\"\",\"liverFunctionCb\":\"5.3\",\"healthGuidanceOtherStr\":\"定期接受健管师健康教育指导\",\"pulseFrequency\":\"90\",\"elderlyDepressionCheck\":\"\",\"dryOutAge\":\"\",\"smokingCircumstance\":\"1\",\"heartUndiscovered\":\"1\",\"cvdUndiscovered\":\"1\",\"drinkRedWine\":\"\",\"eyeDiseaseOthers\":\"\",\"quitSmokingAge\":\"\",\"cardiacSouffleOthers\":\"\",\"hazardOthersStr\":\"定期复查各项异常指标,严格糖尿病饮食，监测血糖，规律服药。\",\"scleraOthers\":\"\",\"lungRhonchus\":\"无\",\"urineRoutineOthers\":\"白细胞+2\",\"symptomHeadache\":\"\",\"eatVegetarianFlash\":\"\",\"cvdOthersStr\":\"\",\"symptomConstipation\":\"\",\"breastNotTroubleFind\":\"\",\"poisonRadiogen\":\"\",\"leukocyte\":\"9.4\",\"waist\":\"93\",\"cvdCerebralHemorrhage\":\"\",\"corpusException\":\"\",\"abdoShiftingDullOth\":\"\",\"nervousSystemDiseaseStr\":\"\",\"drinkOthersStr\":\"\",\"skin\":\"正常\",\"cervicalPapSmearsExcep\":\"\",\"healthGuidanceInhospital\":\"1\",\"hazardOthers\":\"1\",\"healthRegularFollowUp\":\"1\",\"hazardVaccination\":\"\",\"poisonChemicalIspre\":\"\",\"abdoPressPainOth\":\"\",\"healthGuidanceSlowDisease\":\"1\",\"bloodPressureRigthD\":\"62\",\"cervix\":\"\",\"poisonDustIspre\":\"\",\"breastMasses\":\"\",\"attachmentException\":\"\",\"nervousSystemDisease\":\"1\",\"hazardHardening\":\"1\",\"cervixException\":\"\",\"symptomPolyuria\":\"\",\"lungBreathSound\":\"1\",\"symptomDyspnea\":\"\",\"cxrException\":\"\",\"everyAlcohol_tolerance\":\"\",\"microalbuminuria\":\"200\",\"corporeityYinDeficiency\":\"1\",\"fundamentFingerp\":\"\",\"elderlyCognitiveFun\":\"1\",\"dentitiondentureMissTeeth\":\"\",\"hbsag\":\"\",\"eyeground\":\"\",\"renalAgn\":\"\",\"cardiacRhythm\":\"不齐\",\"bmi\":\"23.67\",\"oneYearIsTemulentia\":\"\",\"corporeityQiAsthenia\":\"\",\"pharyngealportionYes\":\"\",\"straightenVisionRightEye\":\"\",\"symptomExpectoration\":\"\",\"hazardVaccinationStr\":\"\",\"bloodFatTc\":\"3.94\",\"symptomChestStuffiness\":\"1\",\"lymphNodeOthers\":\"\",\"bodyTemperature\":\"36.50\",\"typeBUltrasonicExcep\":\"\",\"immersionFoot\":\"无\",\"renalOthersStr\":\"\",\"beginDrinkAge\":\"\",\"symptomNauseaVomiting\":\"\",\"corporeityGentle\":\"\",\"corporeityDampHeat\":\"\",\"orgName\":\"海沧区嵩屿街道社区卫生服务中心\",\"vagina\":\"\",\"heartAnginaPectoris\":\"\",\"urineSugar\":\"-\",\"poisonRadiogenIspre\":\"\",\"drugList\":[{\"drugUsage\":\"tid\",\"drugDate\":\"\",\"drugCompliance\":\"1\",\"drugDosage\":\"50mg\",\"drugName\":\"二甲双胍\"},{\"drugUsage\":\"qd\",\"drugDate\":\"\",\"drugCompliance\":\"1\",\"drugDosage\":\"80mg\",\"drugName\":\"格列齐特\"},{\"drugUsage\":\"bid\",\"drugDate\":\"\",\"drugCompliance\":\"1\",\"drugDosage\":\"23.75mg\",\"drugName\":\"倍他乐克缓释片\"},{\"drugUsage\":\"tid\",\"drugDate\":\"\",\"drugCompliance\":\"\",\"drugDosage\":\"0.5g\",\"drugName\":\"拜糖平\"},{\"drugUsage\":\"qn\",\"drugDate\":\"\",\"drugCompliance\":\"\",\"drugDosage\":\"0.5片\",\"drugName\":\"华法令\"},{\"drugUsage\":\"qd\",\"drugDate\":\"\",\"drugCompliance\":\"\",\"drugDosage\":\"1片\",\"drugName\":\"地高辛\"}],\"drinkWhiteSpirits\":\"\",\"bloodPressureRigthU\":\"127\",\"liverFunctionAlbumin\":\"37.3\",\"visionLeftEye\":\"4.5\",\"straightenVisionLeftEye\":\"\",\"heartRate\":\"90\",\"symptomPolydipsia\":\"\",\"bloodPressureLeftU\":\"\",\"physicalExaminationOth\":\"\",\"dailySmokingQuantity\":\"\",\"drinkYellowWine\":\"\",\"drinkBeer\":\"\",\"eatHobbySalt\":\"\",\"pharyngealportionAdd\":\"\",\"symptomOtherStr\":\"\",\"elderlySelfCare\":\"可自理（0～3分）\",\"dentitiondentureDenture\":\"1\",\"angiosisOthersStr\":\"\",\"eyeDiseaseOthersStr\":\"\",\"hazardQuitSmocking\":\"\",\"symptomLackOfPower\":\"1\",\"lymphNode\":\"\",\"angiosisOcclusionArteries\":\"\",\"hemoglobin\":\"119\",\"eyeDiseaseUndiscovered\":\"1\",\"insistHardeningTime\":\"\",\"renalUndiscovered\":\"1\",\"attachment\":\"\",\"healthGuidanceReview\":\"\",\"liverFunctionSalt\":\"44.3\",\"cvdTia\":\"\",\"medicalTime\":\"2016-02-26 08:07:00\",\"hazardFood\":\"1\",\"symptomBlurredVision\":\"\",\"corporeityPhlegmDamp\":\"\",\"hazardLoseWeightTarget\":\"\",\"poisonPhysicalIspre\":\"\",\"corporeityHaemostasis\":\"\",\"heartCoronaryArtery\":\"\",\"poisonDust\":\"\",\"pharyngealportionNo\":\"1\",\"eatClitocybineEqualization\":\"1\",\"heartMyocardialInfarction\":\"\",\"electrocardiogramExcep\":\"心脏起搏器\"},\"event3\":\"{\\\"EhrCount\\\":[{\\\"COUNT\\\":175}],\\\"EhrList\\\":[{\\\"XMAN_ID\\\":\\\"19a5cb41-3c5a-4a5d-b197-aeec6f70d52d\\\",\\\"EVENT\\\":\\\"4715b0ac-7020-45f6-af51-681c29603cf3\\\",\\\"ORG_ID\\\":41,\\\"END_TIME\\\":\\\"2017-03-01 10:14:47\\\",\\\"TYPE\\\":1,\\\"CATALOG_CODE\\\":\\\"0141\\\",\\\"SERIAL\\\":1,\\\"EHR_COMMIT_TIME\\\":\\\"2017-03-02 05:41:28\\\",\\\"UNIONSSID\\\":\\\"D20950585\\\",\\\"ORG_NAME\\\":\\\"厦门市海沧区海沧街道嵩屿社区卫生服务中心\\\",\\\"MEDICINE\\\":[{\\\"MEDICINE_NAME\\\":\\\"优泌乐\\\",\\\"MEDICINE_CODE\\\":\\\"10808078206701611001\\\"},{\\\"MEDICINE_NAME\\\":\\\"来得时\\\",\\\"MEDICINE_CODE\\\":\\\"10808065206023911001\\\"},{\\\"MEDICINE_NAME\\\":\\\"伤湿止痛膏\\\",\\\"MEDICINE_CODE\\\":\\\"21803125173245910101\\\"},{\\\"MEDICINE_NAME\\\":\\\"可立\\\",\\\"MEDICINE_CODE\\\":\\\"11501002109181910101\\\"},{\\\"MEDICINE_NAME\\\":\\\"螺内酯\\\",\\\"MEDICINE_CODE\\\":\\\"11601003109715710501\\\"}],\\\"R\\\":1}]}\",\"event2\":[{\"orgName\":\"厦门市海沧医院\",\"createTime\":\"2016-11-02 00:00:00\",\"patient\":\"915d52a7-5b1d-11e6-8344-fa163e8aee56\",\"dataFrom\":\"1\",\"id\":\"b12873f7-679c-4b09-9c59-98254eb01d21\",\"eventType\":\"2\",\"dianosis\":\"2型糖尿病\",\"eventDate\":\"2016-11-02 00:00:00\"}]},\"patient\":\"915d52a7-5b1d-11e6-8344-fa163e8aee56\",\"businessType\":7},\"time\":\"2017-03-02 11:05:20\"}"));
        System.out.println(new JSONArray(portraits).toString());
    }

}
