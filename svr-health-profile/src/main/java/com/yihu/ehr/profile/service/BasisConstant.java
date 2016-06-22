package com.yihu.ehr.profile.service;


/**
 * 档案基础常量
 * @author hzp 2016-05-26
 */
public class BasisConstant {

    /********** 资源代码 ******************/
    public static String patientInfo = "RS_PATIENT_INFO";//患者基本信息资源
    public static String patientEvent = "RS_PATIENT_EVENT";//患者门诊/住院事件资源

    public static String medicationMaster = "RS_MEDICATION_MASTER";//患者处方主表
    public static String medicationPrescription = "RS_MEDICATION_PRESCRIPTION";//处方笺
    public static String medicationChinese = "RS_MEDICATION_CHINESE";//患者用中药药历史记录资源
    public static String medicationWestern = "RS_MEDICATION_WESTERN";//患者用西药药历史记录资源
    public static String medicationStat = "RS_MEDICATION_STAT";//患者用药统计资源

    public static String outpatientDiagnosis = "RS_OUTPATIENT_DIAGNOSIS";//门诊诊断
    public static String outpatientSymptom = "RS_OUTPATIENT_SYMPTOM";//门诊症状
    public static String outpatientCost = "RS_OUTPATIENT_COST";	//门诊:费用汇总
    public static String outpatientCostDetail = "RS_OUTPATIENT_COST_DETAIL";	//门诊:费用明细

    public static String hospitalizedDiagnosis = "RS_HOSPITALIZED_DIAGNOSIS";//住院诊断
    public static String hospitalizedSymptom = "RS_HOSPITALIZED_SYMPTOM";//住院症状
    public static String hospitalizedCost = "RS_HOSPITALIZED_COST";	//住院:费用汇总
    public static String hospitalizedCostDetail = "RS_HOSPITALIZED_COST_DETAIL";	//住院:费用明细
    public static String hospitalizedOrdersTemporary = "RS_HOSPITALIZED_ORDERS_TEMPORARY";	//住院临时医嘱
    public static String hospitalizedOrdersLongtime = "RS_HOSPITALIZED_ORDERS_LONGTIME";	//住院长期医嘱
    public static String hospitalizedDeath = "RS_HOSPITALIZED_DEATH";	//住院死亡记录

    public static String examinationReport = "RS_EXAMINATION_REPORT";//检查报告
    public static String examinationImg = "RS_EXAMINATION_IMG";//检查报告图片
    public static String laboratoryReport = "RS_LABORATORY_REPORT";	//检验报告
    public static String laboratoryProject = "RS_LABORATORY_PROJECT";	//检验报告项目
    public static String laboratoryImg = "RS_LABORATORY_IMG";	//检验报告图片
    public static String laboratoryAllergy = "RS_LABORATORY_ALLERGY";	//患者检验药敏
    public static String surgery = "RS_SURGERY";	//患者手术记录


    /********** 数据元 *******************/
    public static String profileId = "profile_id";//细表外键profile_id
    public static String mzzd = "EHR_000109";//门诊诊断代码
    public static String zyzd = "EHR_000293";//住院诊断代码
    public static String cfbh = "EHR_000086";//处方编号

    public static String xybm = "EHR_000099";//西药编码
    public static String xysj = "EHR_000090";//西药处方时间
    public static String xymc = "EHR_000100";//西药名称
    public static String xysl = "EHR_000104";//西药数量

    public static String zybm = "EHR_000130";//中药编码
    public static String zysj = "EHR_000120";//中药处方时间
    public static String zymc = "EHR_000131";//中药名称
    public static String zysl = "EHR_000139";//中药数量



    public static String jyzb = "EHR_000392";//检验指标
    public static String jysj = "EHR_000384";//检验时间

    public static String mzfysj="EHR_000054";//门诊费用时间
    public static String zyfysj="EHR_000183";//门诊费用时间


    //处方签模板标识
    public static String xycd = "HSDC01.04"; //西药CDA_CODE
    public static String zycd = "HSDC01.05"; //中药CDA_CODE

}
