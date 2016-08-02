package com.yihu.ehr.medicalRecord.family;


import java.util.Date;

/**
 * @author hzp
 * @created 2016.07.27
 */
public class TextFamily {
    public static final String TableName = "HBMR_TEXT_LIST";
    public static final String Data = "data";

    public static class DataColumns {
        public static final String Content       = "content"; //内容
        public static final String Creater       = "creater";//创建者id
        public static final String CreaterName   = "creater_name";//创建者name
        public static final String CreateTime    = "create_time";//创建时间
        public static final String PatientId     = "patient_id";//病人ID
        public static final String PatientName     = "patient_name";//病人name
        public static final String BusinessClass = "business_class";//业务类型 0 治疗建议 1 病情描述
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                TextFamily.Data
        };
    }

    /**
     * 获取指定族的列.
     *
     * @return
     */
    public static String[] getColumns(String family) {
        if (family.equals(Data)) {
            return new String[]{
                DataColumns.Content,
                DataColumns.Creater,
                DataColumns.CreaterName,
                DataColumns.CreateTime,
                DataColumns.PatientId,
                DataColumns.PatientName,
                DataColumns.BusinessClass
            };
        }

        return null;
    }


    public static String getRowkey(String creater,String patientId){
        if(patientId!=null && patientId.length()>0)
        {
            return creater+"_"+patientId+"_"+(Long.MAX_VALUE -new Date().getTime());
        }
        else{
            return creater+"_"+creater+"_"+(Long.MAX_VALUE -new Date().getTime());
        }
    }

}
