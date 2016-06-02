package com.yihu.ehr.profile.service;


import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.profile.feign.XOrganizationClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 档案基础常量
 * @author hzp 2016-05-26
 */
public class BasisConstant {

    /********** 资源代码 ******************/
    public static String patientInfo = "RS_PATIENT_INFO";//患者基本信息资源
    public static String patientEvent = "RS_PATIENT_EVENT";//患者门诊/住院事件资源
    public static String outpatientDiagnosis = "RS_OUTPATIENT_DIAGNOSIS";//患者门诊诊断资源
    public static String hospitalizedDiagnosis = "RS_HOSPITALIZED_DIAGNOSIS";//患者住院诊断资源
    public static String medicationChinese = "RS_MEDICATION_CHINESE";//患者用中药药历史记录资源
    public static String medicationWestern = "RS_MEDICATION_WESTERN";//患者用西药药历史记录资源
    public static String medicationStat = "RS_MEDICATION_STAT";//患者用药统计资源
    public static String outpatientCost = "RS_OUTPATIENT_COST";	//门诊:费用
    public static String hospitalizedCost = "RS_HOSPITALIZED_COST";	//住院:费用
    public static String laboratoryReport = "RS_LABORATORY_REPORT";	//患者检验报告资源
    public static String laboratoryPicture = "RS_LABORATORY_PICTURE";	//患者检验报告图片
    public static String laboratoryAllergy = "RS_LABORATORY_ALLERGY";	//患者检验药敏
    public static String surgery = "RS_SURGERY";	//患者手术记录
    public static String signsData = "RS_SIGNS_DATA";//患者体征记录



    /********** 数据元 *******************/
    public static String profileId = "profile_id";//细表外键profile_id
    public static String mzzd = "EHR_000109";//门诊诊断代码
    public static String zyzd = "EHR_000293";//住院诊断代码
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


}
