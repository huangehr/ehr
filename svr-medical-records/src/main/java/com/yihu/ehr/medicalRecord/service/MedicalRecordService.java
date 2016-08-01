package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.DoctorDao;
import com.yihu.ehr.medicalRecord.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.PatientDao;
import com.yihu.ehr.medicalRecord.dao.hbaseDao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalRecord;
import com.yihu.ehr.medicalRecord.model.Entity.MrDoctorMedicalRecordsEntity;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by shine on 2016/7/14.
 */

@Service
@Transactional
public class MedicalRecordService{

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    @Autowired
    PatientDao patientDao;

    @Autowired
    DoctorDao doctorDao;

    /**
     * 根据医生ID和病人ID获取最近的一次病历
     */
    public Map<String,Object> medicalRecord(String patientId, String doctorId, String ticket, String appUid) throws Exception {

        return null;
    }

    /**
     * 获取病历
     */
    public Map<String,Object> getMedicalRecord(String recordId) throws Exception {

        return null;
    }

    /**
     * 新增病历
     */
    public MedicalRecord addRecord(String doctorId, String patientId) throws Exception {

        return null;
    }


    /**
     * 修改病历
     */
    public boolean editRecord(String recordId, Map<String,Object> map) throws Exception {

        return true;
    }


    /**
     * 删除病历
     */
    public boolean deleteRecord(String recordId) throws Exception {

        return true;
    }

    /**
     * 病历分享
     */
    public boolean shareRecord(String recordId,String patientId,String doctorId) throws Exception {

        return true;
    }
}