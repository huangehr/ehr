package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */

@Service
@Transactional
public class MedicalRecordService{

    @Autowired
    MedicalRecordDao mrDao;

    @Autowired
    DoctorMedicalRecordDao dMRDao;

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
    public MrMedicalRecordsEntity getRecordByLastOne(int patientId, String doctorId) throws Exception {

        MrMedicalRecordsEntity medicalRecord = new MrMedicalRecordsEntity();

        //获取最近一次的就诊病历
        List<MrMedicalRecordsEntity> medicalRecordList = mrDao.findBypatientIdAndDoctorId(patientId,doctorId);

        if(medicalRecordList.size() > 0){

            medicalRecord = medicalRecordList.get(0);
        }else{

            Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            medicalRecord.setPatientId(patientId);
            medicalRecord.setDoctorId(doctorId);
            medicalRecord.setMedicalTime(t);
            medicalRecord = mrDao.save(medicalRecord);

            MrDoctorMedicalRecordsEntity drRelation = new MrDoctorMedicalRecordsEntity();

            drRelation.setRecordId(medicalRecord.getId());
            drRelation.setDoctorId(medicalRecord.getDoctorId());
            drRelation.setIsCreator("1");//创建者
            drRelation.setRecordType("0");//线上诊断

            dMRDao.save(drRelation);
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

    public MrMedicalRecordsEntity copyRecord(int patientId, String doctorId, int id, Integer firstRecordId)
    {
        MrMedicalRecordsEntity newRecord = new MrMedicalRecordsEntity();
        MrMedicalRecordsEntity templateRecord = mrDao.findByid(id);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());

        newRecord.setPatientId(patientId);
        newRecord.setDoctorId(doctorId);
        newRecord.setMedicalTime(t);

        if(firstRecordId != null){
            newRecord.setFirstRecordId(firstRecordId);
        }

        newRecord.setMedicalDiagnosis(templateRecord.getMedicalDiagnosis());
        newRecord.setMedicalDiagnosisCode(templateRecord.getMedicalDiagnosisCode());
        newRecord.setMedicalSuggest(templateRecord.getMedicalSuggest());
        newRecord.setPatientAllergy(templateRecord.getPatientAllergy());
        newRecord.setPatientCondition(templateRecord.getPatientCondition());
        newRecord.setPatientHistoryNow(templateRecord.getPatientHistoryNow());
        newRecord.setPatientHistoryPast(templateRecord.getPatientHistoryPast());
        newRecord.setPatientHistoryFamily(templateRecord.getPatientHistoryFamily());
        newRecord.setPatientPhysical(templateRecord.getPatientPhysical());

        return mrDao.save(newRecord);
    }
}