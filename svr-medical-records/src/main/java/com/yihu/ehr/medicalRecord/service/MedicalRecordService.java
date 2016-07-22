package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */

@Service
@Transactional
public class MedicalRecordService{

    @Autowired
    MedicalRecordDao mrDao;


    /**
     * 创建数据元
     *
     * @param medicalRecord MrMedicalRecordsEntity 数据元
     * @return MrMedicalRecordsEntity 数据元
     */
    public MrMedicalRecordsEntity saveMedicalRecord(MrMedicalRecordsEntity medicalRecord){

        return mrDao.save(medicalRecord);
    }

    /**
     * 根据医生ID和病人ID获取最近的一次病历
     */
    public MrMedicalRecordsEntity getMedicalHistoryByLastOne(String doctorId, int patientId) throws Exception {

        MrMedicalRecordsEntity medicalRecord = new MrMedicalRecordsEntity();

        //获取最近一次的就诊病历
        List<MrMedicalRecordsEntity> medicalRecordList = mrDao.findBypatientIdAndDoctorId(patientId,doctorId);

        if(medicalRecordList.size() > 0){

            medicalRecord = medicalRecordList.get(0);
        }

        return medicalRecord;
    }

    /**
     * 删除数据元
     *
     * @param id int 数据元ID
     */
    public void deleteRecord(int id)
    {

        MrMedicalRecordsEntity medicalRecord = mrDao.findByid(id);
        mrDao.delete(medicalRecord);
    }

    /**
     * 获取病历
     *
     * @param id int 数据元ID
     */
    public MrMedicalRecordsEntity getMedicalRecord(int id)
    {

        return mrDao.findByid(id);
    }
}