package com.yihu.ehr.medicalRecord.family;


import java.util.Date;

/**
 * 文档HBase列族结构: |data|。
 *
 * @author hzp
 * @created 2016.07.27
 */
public class DocumentFamily {

    public static final String TableName = "EHR_DOCUMENT_LIST";
    public static final String Data      = "Data";

    public static class DataColumns {
        public static final String DocumentName       = "document_name"; //文件名
        public static final String CreateTime         = "create_time";//文件创建时间
        public static final String Creater            = "creater";//上传用户id
        public static final String PatientId          = "patient_id";//病人ID
        public static final String DocumentContent    = "document_content";//文档摘要
        public static final String FileType           = "file_type";//文件格式类型（图片/PDF）
        public static final String FileUrl            = "file_url";//文件源url
        public static final String DataFrom           = "data_from";//数据来源0 院内采集 1 用户上传 2 病历夹上传
        public static final String DocumentType       = "document_type";//文档类型
        public static final String MedicalInstitution = "medical_institution";//医疗机构
        public static final String CertificateType    = "certificate_type";//证件类型
        public static final String CertificateId      = "certificate_id";//证件号
        public static final String EventNo            = "event_no";//事件id
        public static final String FileRemoteStorage  = "file_remote_storage";//文件远程存储地址
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                DocumentFamily.Data,
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
                DataColumns.DocumentName,
                DataColumns.CreateTime,
                DataColumns.Creater,
                DataColumns.PatientId,
                DataColumns.DocumentContent,
                DataColumns.FileType,
                DataColumns.FileUrl,
                DataColumns.DataFrom,
                DataColumns.DocumentType,
                DataColumns.MedicalInstitution,
                DataColumns.CertificateType,
                DataColumns.CertificateId,
                DataColumns.EventNo,
                DataColumns.FileRemoteStorage
            };
        }

        return null;
    }

    public static String getRowkey(String creater,String patientId){
        if(patientId!=null && patientId.length()>0)
        {
            return creater+"_"+patientId+"_"+Long.toString(new Date().getTime());
        }
        else{
            return creater+"_"+creater+"_"+Long.toString(new Date().getTime());
        }
    }

    public String getTableName(){
        return TableName;
    }
}
