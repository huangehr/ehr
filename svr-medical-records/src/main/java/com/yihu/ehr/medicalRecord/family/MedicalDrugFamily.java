package com.yihu.ehr.medicalRecord.family;

import java.util.Date;

/**
 * 病历用药HBase列族结构: |data|。
 *
 * Created by Guo Yanshan on 2016/7/27.
 */
public class MedicalDrugFamily {

    public static final String TableName = "EHR_MEDICAL_DRUG";
    public static final String Data      = "data";

    public static class DataColumns {
        public static final String RecordRowkey       = "record_rowkey";       //病历rowkey
        public static final String PrescriptionRowkey = "prescription_rowkey"; //处方rowkey
        public static final String DrugName           = "drug_name";           //药品名称
        public static final String DrugSpecifications = "drug_specifications"; //药品规格
        public static final String DrugQuantity       = "drug_quantity";       //药品数量
        public static final String DrugUnit           = "drug_unit";           //药品单位
        public static final String DrugUse            = "drug_use";            //药品用法
        public static final String DrugDosage         = "drug_dosage";         //药品用量
        public static final String DrugFrequency      = "drug_frequency";      //用药频次
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
            MedicalDrugFamily.Data,
        };
    }



    public static String getRowkey(String recordRowkey, String PrescriptionRowkey){

        return recordRowkey + "_" + PrescriptionRowkey + "_" + Long.toString(new Date().getTime());
    }

}
