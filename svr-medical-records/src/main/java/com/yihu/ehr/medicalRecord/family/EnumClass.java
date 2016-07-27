package com.yihu.ehr.medicalRecord.family;


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
}
