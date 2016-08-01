package com.yihu.ehr.medicalRecord.model;


import java.util.Date;

/**
 * @author hzp
 * @created 2016.07.27
 */
public class EnumClass {
    /**
     * 文件上传来源
     */
    public static class DocumentDataFrom {
        public static final String DataCollection = "0"; //0 院内采集
        public static final String BrowserUpload  = "1";// 1 档案浏览器上传
        public static final String MedicalUpload  = "2";// 2 病历夹上传
    }

    /**
     * 业务分类
     */
    public static class BusinessClass {
        public static final String DoctorOrder         = "0"; //0 治疗建议（医嘱）
        public static final String DiseaseDescription  = "1";// 1 病情描述
    }

    /**
     * 标签类型
     */
    public static class LabelType {
        public static final String MedicalLabel    = "0"; //0 病历标签
        public static final String MatericalLabel  = "1";// 1 素材标签
    }

    /**
     * 病历类型
     */
    public static class RecordType {
        public static final String Online    = "0"; //0 线上诊断
        public static final String Outpatient  = "1";// 1 门诊
        public static final String Hospitalized  = "2";// 住院
        public static final String Examination  = "3";// 体检
    }

    /**
     * 字段类型
     */
    public static class FieldType {
        public static final String String    = "0"; //0 字符串
        public static final String Date  = "1";// 1 时间
        public static final String Int  = "2";// 数值
        public static final String Img  = "3";// 图片
    }

    /**
     * 模板类型
     */
    public static class TemplateType {
        public static final String Condition    = "patient_condition"; //病情主诉
        public static final String HistoryNow  = "patient_history_now";// 现病史
        public static final String HistoryPast  = "patient_history_past";// 既往史
        public static final String Allergy  = "patient_allergy";// 过敏史
        public static final String Family  = "patient_history_family";// 家族史
        public static final String Physical  = "patient_physical";// 体格检查
    }

    /**
     * 性别类型
     */
    public static class Sex {
        public static final String Man  = "1";// 男
        public static final String Woman  = "2";// 女
        public static final String Unknow  = "3";// 未知
    }

    /**
     * 婚否
     */
    public static class IsMarried {
        public static final String Married  = "1";// 已婚
        public static final String Unmarried  = "2";// 未婚
        public static final String Unknow  = "3";// 未知
    }

}
