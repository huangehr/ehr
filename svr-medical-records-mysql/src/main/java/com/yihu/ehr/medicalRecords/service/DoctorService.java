package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorDao;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsQueryDao;
import com.yihu.ehr.medicalRecords.model.DTO.DictDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */
@Transactional
@Service
public class DoctorService {

    @Autowired
    DoctorDao doctorDao;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    PatientService patientService;

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    @Autowired
    MedicalRecordsQueryDao medicalRecordsQueryDao;

    @Autowired
    MedicalLabelService medicalLabelService;


    /**
     * 获取医生信息,不存在则新增，存在则修改
     */
    public MrDoctorsEntity getDoctorInformation(String doctorId)throws Exception
    {
        MrDoctorsEntity re = new MrDoctorsEntity();
        WlyyResponse response = wlyyService.queryDoctorInfoByID(doctorId);
        //获取医生信息成功
        if(response.getStatus() == 200)
        {
            Map<String,Object> map = (Map<String,Object>)response.getData();
            if (map!=null && map.size()>0) {
                MrDoctorsEntity doctor = new MrDoctorsEntity();
                doctor.setId(doctorId);
                doctor.setName(map.get("name").toString());
                //doctor.setDemographicId(map.get("").toString());
                doctor.setSex(map.get("sex").toString());
                if (map.get("birthday") != null && map.get("birthday").toString().length() > 0) {
                    doctor.setBirthday(java.sql.Timestamp.valueOf(map.get("birthday").toString()));
                }
                doctor.setPhoto(map.get("photo").toString());
                doctor.setPhone(map.get("mobile").toString());

                doctor.setTitle(map.get("jobName").toString());
                doctor.setGood(map.get("expertise").toString());
                doctor.setOrgName(map.get("hospital_name").toString());
                doctor.setOrgDept(map.get("dept_name").toString());
                doctorDao.save(doctor);
                re = doctor;
            }
        }
        else {
            re = doctorDao.findById(doctorId);
            Message.debug(response.getMsg());
        }
        return re;
    }

    /**
     * 获取医生信息
     */
    public MrDoctorsEntity getDoctor(String doctorId) throws Exception
    {
        return doctorDao.findById(doctorId);
    }


    /**
     * 保存医生信息
     */
    public boolean saveDoctor(MrDoctorsEntity doctor){
        doctorDao.save(doctor);
        return true;
    }


    /**
     * 获取医生诊断
     */
    public List<DictDTO> getDoctorDiagnosis(String doctorId)throws Exception {

        return  medicalRecordsQueryDao.findDoctorDiagnosis(doctorId);
    }

    /**
     * 获取医生病历
     */
    public Envelop getDoctorRecords(String doctorId, String label, String medicalTimeFrom, String medicalTimeEnd, String recordType, int ageFrom, int ageEnd, String sex, String filter, int page, int size) throws Exception {

        String sql = "SELECT concat(a.id) as id,a.medical_Time as medicalTime,'0' as medicalType,a.MEDICAL_DIAGNOSIS as medicalDiagnosis,b.name as doctorName,b.org_dept as orgDept,b.org_name as orgName,b.title as doctorTitle,c.medical_info as medicalInfo\n" +
                "from mr_medical_records a \n" +
                "left join mr_doctors b on a.DOCTOR_ID=b.ID\n" +
                "left join (SELECT record_id, GROUP_CONCAT(value) AS medical_info FROM mr_medical_info GROUP BY record_id) c on a.id = c.record_id \n" +
                "where doctor_id='"+doctorId+"'";

        return  medicalRecordsQueryDao.queryPage(sql,page,size);
    }
}
