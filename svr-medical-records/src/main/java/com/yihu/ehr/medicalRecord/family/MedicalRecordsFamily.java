package com.yihu.ehr.medicalRecord.family;

import java.util.Date;

/**
 * 病历HBase列族结构: |data|dynamic|。
 *
 * @author Guo Yanshan
 * @created 2016.07.26
 */

public class MedicalRecordsFamily {

    public static final String TableName  = "HBMR_MEDICAL_RECORDS";

    public static final String Data       = "data";
    public static final String Dynamic    = "dynamic";

    public static class DataColumns {

        public static final String CreateTime           = "create_time";              //创建时间
        public static final String MedicalTime           = "medical_time";              //就诊时间
        public static final String DoctorId             = "doctor_id";                //医生ID
        public static final String DoctorName           = "doctor_name";              //医生姓名
        public static final String Title           = "title";              //职称
        public static final String OrgDept              = "org_dept";                 //就诊科室
        public static final String OrgName              = "org_name";                 //就诊机构
        public static final String PatientId            = "patient_id";               //病人ID
        public static final String PatientName          = "patient_name";             //病人姓名
        public static final String DemographicId        = "demographic_id";           //病人身份证
        public static final String Sex                  = "sex";                      //性别代码 1男 2女 3未知
        public static final String Birthday             = "birthday";                 //生日
        public static final String IsMarried        = "is_married";           //是否已婚 1已婚 2未婚 3未知
        public static final String Phone             = "phone";                 //手机
        public static final String MedicalDiagnosis     = "medical_diagnosis";        //诊断
        public static final String MedicalDiagnosisCode = "medical_diagnosis_code";   //诊断代码
        public static final String MedicalSuggest       = "medical_suggest";          //治疗建议
        public static final String PatientCondition     = "patient_condition";        //病情主诉
        public static final String PatientHistoryNow    = "patient_history_now";      //现病史
        public static final String PatientHistoryPast   = "patient_history_past";     //既往史
        public static final String PatientAllergy       = "patient_allergy";          //过敏史
        public static final String PatientHistoryFamily = "patient_history_family";   //家族史
        public static final String PatientPhysical = "patient_physical";   //体格检查
        public static final String FirstRecordId        = "first_record_id";          //首诊病历ID
        public static final String DataFrom             = "data_from";                //数据来源 1病历夹
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                MedicalRecordsFamily.Data,
                MedicalRecordsFamily.Dynamic
        };
    }

    /**
     * 生成rowkey
     *
     * @param patientId
     * @param dataFrom
     * @return
     */
    public String getRowkey(String patientId, String dataFrom){

        return patientId + "_" + dataFrom + "_" + Long.toString(new Date().getTime());
    }

    public String getTableName(){
        return TableName;
    }
}
