package com.yihu.ehr.medicalRecords.service;

import com.ctc.wstx.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsQueryDao;
import com.yihu.ehr.medicalRecords.dao.PatientDao;
import com.yihu.ehr.medicalRecords.model.DTO.DictDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordSimpleDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */
@Transactional
@Service
public class PatientService {

    @Autowired
    PatientDao patientDao;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    @Autowired
    MedicalRecordsQueryDao medicalRecordsQueryDao;

    @Autowired
    MedicalLabelService medicalLabelService;


    /**
     * 获取患者信息
     */
    public MrPatientsEntity getPatient(String patientId) throws Exception
    {
        return patientDao.findById(patientId);
    }

    /**
     * 保存患者信息
     */
    public boolean savePatient(MrPatientsEntity patient) throws Exception
    {
        patientDao.save(patient);
        return true;
    }

    /**
     * 获取患者信息，不存在则新增
     * @param patientId
     * @return
     * @throws Exception
     */
    public MrPatientsEntity getPatientInformation(String patientId) throws Exception {
        MrPatientsEntity re = new MrPatientsEntity();

        WlyyResponse response = wlyyService.queryPatientInfoByID(patientId);
        //获取患者信息成功
        if(response.getStatus() == 200)
        {
            Map<String,Object> map = (Map<String,Object>)response.getData();

            MrPatientsEntity patient = new MrPatientsEntity();
            patient.setId(patientId);
            patient.setName(map.get("name").toString());
            patient.setDemographicId(map.get("idCard").toString());
            patient.setSex(map.get("sex").toString());
            if (map.get("birthday") != null && map.get("birthday").toString().length() > 0) {
                patient.setBirthday(java.sql.Timestamp.valueOf(map.get("birthday").toString()));
            }
            patient.setMaritalStatus(EnumClass.IsMarried.Unknow);
            patient.setPhoto(map.get("photo").toString());
            patient.setPhone(map.get("mobile").toString());
            patientDao.save(patient);
            re = patient;
        }
        else{
            re = patientDao.findById(patientId);
            Message.debug(response.getMsg());
            }

        return re;
    }



    /**
     * 获取患者诊断
     */
    public List<DictDTO> getPatientDiagnosis(String patientId, String doctorId) throws Exception{
        return medicalRecordsQueryDao.findPatientDiagnosis(doctorId,patientId);
    }

    /**
     * 获取患者病历
     */
    public Envelop getPatientRecords(String doctorId, String patientId, String label, String medicalTimeFrom, String medicalTimeEnd, String recordType, String medicalDiagnosisCode, String filter, int page, int size) throws Exception {

        String sql = "SELECT concat(a.id) as id,a.medical_Time as medicalTime,'0' as medicalType,a.MEDICAL_DIAGNOSIS as medicalDiagnosis,b.name as doctorName,b.org_dept as orgDept,b.org_name as orgName,b.title as doctorTitle,c.medical_info as medicalInfo\n" +
                "from mr_medical_records a \n" +
                "left join mr_doctors b on a.DOCTOR_ID=b.ID\n" +
                "left join (SELECT record_id, GROUP_CONCAT(value) AS medical_info FROM mr_medical_info GROUP BY record_id) c on a.id = c.record_id \n" +
                "where a.doctor_id='"+doctorId+"' and a.patient_id='"+patientId+"'";
        //自定义标签
        if(label!=null && label.length()>0)
        {
            String[] labels = label.split(",");
            String labelString = "";
            for(String item:labels)
            {
                labelString += "'"+item+"',";
            }
            labelString = labelString.substring(0,labelString.length()-1);
            sql += " and a.id in ("+labelString+")";
        }

        //就诊开始时间
        if(!StringUtils.isEmpty(medicalTimeFrom))
        {
            sql += "";
        }

        //就诊结束时间
        if(!StringUtils.isEmpty(medicalTimeEnd))
        {
            sql += "";
        }

        //就诊类型
        if(!StringUtils.isEmpty(recordType) && !recordType.equals(EnumClass.RecordType.Online))
        {
            sql += " and 1!=1";
        }

        //就诊代码
        if(!StringUtils.isEmpty(medicalDiagnosisCode))
        {
            sql += " and a.medical_diagnosis_code = '"+medicalDiagnosisCode+"'";
        }
        //过滤条件
        if(!StringUtils.isEmpty(filter))
        {
            sql += " and (a.MEDICAL_DIAGNOSIS like '%"+filter+"%' or b.medical_info like '%"+filter+"%')";
        }

        return  medicalRecordsQueryDao.queryPage(sql,page,size);
    }

}
