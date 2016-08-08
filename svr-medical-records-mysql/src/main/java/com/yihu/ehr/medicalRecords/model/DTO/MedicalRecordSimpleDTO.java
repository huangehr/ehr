package com.yihu.ehr.medicalRecords.model.DTO;

import java.util.Date;

/**
 * Created by hzp on 2016/8/8.
 *  简单病历，用于列表搜索展示
 */
public class MedicalRecordSimpleDTO {

    private String  id;
    private Date medicalTime;
    private String medicalType;
    private String orgName;
    private String orgDept;
    private String doctorName;
    private String doctorTitle;
    private String medicalDiagnosis;

}
