package com.yihu.ehr.profile.util;

public class BasicConstant {

    /********** 资源代码 ******************/
    public static String patientInfo = "RS_PATIENT_INFO";//患者基本信息资源
    public static String patientEvent = "RS_PATIENT_EVENT";//患者门诊/住院事件资源

    public static String medicationMaster = "RS_MEDICATION_MASTER";//患者处方主表
    public static String medicationPrescription = "RS_MEDICATION_PRESCRIPTION";//处方笺
    public static String medicationChinese = "RS_MEDICATION_CHINESE";//患者用中药药历史记录资源
    public static String medicationWestern = "RS_MEDICATION_WESTERN";//患者用西药药历史记录资源
    public static String medicationWesternStat = "RS_MEDICATION_WESTERN_STAT";//患者西药用药统计资源
    public static String medicationChineseStat = "RS_MEDICATION_CHINESE_STAT";//患者中药用药统计资源

    public static String hospitalizedOrdersTemporary = "RS_HOSPITALIZED_ORDERS_TEMPORARY";	//住院临时医嘱
    public static String hospitalizedOrdersLongtime = "RS_HOSPITALIZED_ORDERS_LONGTIME";	//住院长期医嘱

    public static String laboratoryProject = "RS_LABORATORY_PROJECT";	//检验报告项目

    public static String cfbh = "EHR_000086";//处方编号

    public static String xysj = "EHR_000090";//西药处方时间
    public static String xymc = "EHR_000100";//西药名称

    public static String zysj = "EHR_000120";//中药处方时间
    public static String zymc = "EHR_000131";//中药名称

    //处方签模板标识
    public static String xycd = "HSDC01.04"; //西药CDA_CODE
    public static String zycd = "HSDC01.05"; //中药CDA_CODE

}
