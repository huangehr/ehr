package com.yihu.ehr.medicalRecord.service.record;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 病历临时存储工具。此阶段也是存在于内存中，之后会存入hbase。
 *
 * Created by Guo Yanshan on 2016/7/27.
 */
public class RecordBucket {

    private Date   CreateTime;             //创建时间
    private String DoctorId;               //医生ID
    private String DoctorName;             //医生姓名
    private String OrgDept;                //就诊科室
    private String OrgName;                //就诊机构
    private String PatientId;              //病人ID
    private String PatientName;            //病人姓名
    private String DemographicId;          //病人身份证
    private String Sex;                    //性别代码 1男 2女 3未知
    private String Birthday;               //生日
    private String MaritalStatus;          //是否已婚 1已婚 2未婚 3未知
    private String MedicalDiagnosis;       //诊断
    private String MedicalDiagnosisCode;   //诊断代码
    private String MedicalSuggest;         //治疗建议
    private String PatientCondition;       //病情主诉
    private String PatientHistoryNow;      //现病史
    private String PatientHistoryPast;     //既往史
    private String PatientAllergy;         //过敏史
    private String PatientHistoryFamily;   //家族史
    private String FirstRecordId;          //首诊病历ID
    private String DataFrom;               //数据来源 1病历夹


    // 病历报告列表，Key为HBase rowkey
    private Map<String, ReportDocument> reportDocument = new TreeMap<>();
}
