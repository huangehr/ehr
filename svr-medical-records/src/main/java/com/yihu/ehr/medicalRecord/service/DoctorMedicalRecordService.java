package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class DoctorMedicalRecordService {

    @Autowired
    DoctorMedicalRecordDao dMRDao;

    public List<MrDoctorMedicalRecordsEntity> getInfoByDocIdrecId(String doctorId){

         return  dMRDao.findBydoctorId(doctorId);
    }

    /**
     * 创建数据元
     *
     * @param doctorMedicalRecord MrDoctorMedicalRecordsEntity 数据元
     * @return MrDoctorMedicalRecordsEntity 数据元
     */
    public MrDoctorMedicalRecordsEntity saveDoctorMedicalRecord(MrDoctorMedicalRecordsEntity doctorMedicalRecord){

        return dMRDao.save(doctorMedicalRecord);
    }

    /**
     * 删除数据元
     */
    public boolean deleteRecords(int recordId){

        List<MrDoctorMedicalRecordsEntity> deleteList = dMRDao.findByrecordId(recordId);
        dMRDao.delete(deleteList);

        return true;
    }

    /**
     * 取消医生病历关联
     */
    public boolean deleteRecordRelation(String doctorId, int recordId){

        List<MrDoctorMedicalRecordsEntity> relation = dMRDao.findBydoctorIdAndRecordId(doctorId, recordId);
        dMRDao.delete(relation);

        return true;
    }

    public List<MrDoctorMedicalRecordsEntity> getDoctorInfoByRecordId(int recordId){

        return  dMRDao.findByrecordId(recordId);
    }
}