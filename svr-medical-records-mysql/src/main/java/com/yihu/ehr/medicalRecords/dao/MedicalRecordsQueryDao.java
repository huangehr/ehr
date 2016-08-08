package com.yihu.ehr.medicalRecords.dao;


import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.model.DTO.DictDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import com.yihu.ehr.medicalRecords.service.DoctorService;
import com.yihu.ehr.medicalRecords.service.PatientService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/8.
 */
@Service
public class MedicalRecordsQueryDao {

    @PersistenceContext
    protected EntityManager entityManager;

    protected Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * 获取医生ICD10
     */
    public List<DictDTO> findDoctorDiagnosis(String doctorId)
    {
        String sql = "select DISTINCT m.medical_diagnosis as name,m.medical_diagnosis_code as code from mr_medical_records m where m.medical_diagnosis_code is not null and m.medical_diagnosis_code<>'' and m.DOCTOR_ID=:doctorId";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameter("doctorId", doctorId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(DictDTO.class));
        return sqlQuery.list();

    }

    /**
     * 获取患者ICD10
     */
    public List<DictDTO> findPatientDiagnosis(String doctorId, String patientId)
    {
        String sql = "select DISTINCT m.medical_diagnosis as name,m.medical_diagnosis_code as code from mr_medical_records m where m.medical_diagnosis_code is not null and m.medical_diagnosis_code<>'' and m.DOCTOR_ID=:doctorId and m.patient_id=:patientId";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameter("doctorId", doctorId);
        sqlQuery.setParameter("patientId", patientId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(DictDTO.class));
        return sqlQuery.list();

    }

    /**
     * 获取医生病历
     */
    public List<DictDTO> query(String doctorId)
    {
        String sql = "select DISTINCT m.medical_diagnosis as name,m.medical_diagnosis_code as code from mr_medical_records m where m.medical_diagnosis_code is not null and m.medical_diagnosis_code<>'' and m.DOCTOR_ID=:doctorId";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameter("doctorId", doctorId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(DictDTO.class));
        return sqlQuery.list();

    }



}