package com.yihu.ehr.medicalRecords.dao;


import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.model.DTO.DictDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordSimpleDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import com.yihu.ehr.medicalRecords.service.DoctorService;
import com.yihu.ehr.medicalRecords.service.PatientService;
import com.yihu.ehr.util.rest.Envelop;
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
    public List<DictDTO> findDoctorDiagnosis(String doctorId) throws Exception
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
    public List<DictDTO> findPatientDiagnosis(String doctorId, String patientId) throws Exception
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
    public List<DictDTO> query(String doctorId) throws Exception
    {
        String sql = "select DISTINCT m.medical_diagnosis as name,m.medical_diagnosis_code as code from mr_medical_records m where m.medical_diagnosis_code is not null and m.medical_diagnosis_code<>'' and m.DOCTOR_ID=:doctorId";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameter("doctorId", doctorId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(DictDTO.class));
        return sqlQuery.list();

    }

    private int defaultPage = 1;
    private int defaultSize = 50;

    /**
     * 获取分页数据
     */
    public Envelop queryPage(String sql,Integer page,Integer size) {
        Envelop re = new Envelop();

        if(page==null)
        {
            page = defaultPage;
        }
        if(size == null)
        {
            size = defaultSize;
        }

        re.setCurrPage(page);
        re.setPageSize(size);
        re.setSuccessFlg(true);

        try {
            SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
            //获取总条数
            int count = sqlQuery.list().size();
            re.setTotalCount(count);

            sqlQuery.setFirstResult(size * (page - 1));
            sqlQuery.setMaxResults(size);
            sqlQuery.setResultTransformer(Transformers.aliasToBean(MedicalRecordSimpleDTO.class));
            re.setDetailModelList(sqlQuery.list());
        }
        catch (Exception ex)
        {
            Message.debug(sql+"执行失败！"+ex.getMessage());
            re.setSuccessFlg(false);
            re.setErrorMsg(ex.getMessage());
        }
        return re;
    }

}