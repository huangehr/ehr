package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class PatientService {


    @Autowired
    PatientDao patientDao;
    public MrPatientsEntity getPatientInformationBydemographicIdOrPhone(String demographicId,String phone){
        if(demographicId==null || patientDao.findBydemographicId(demographicId)==null){
            if(phone!=null&&patientDao.findByphone(phone)!=null)
                return patientDao.findByphone(phone);
            else
                return null;
        }
        else
            return patientDao.findBydemographicId(demographicId);
    }

    public boolean updataPatientInformationByID(MrPatientsEntity patient){

        MrPatientsEntity patientModel=patientDao.findBydemographicId(String.valueOf(patient.getId()));
        if(patient!=null) {
            patientModel.setPhoto(patient.getPhoto());
            patientModel.setName(patient.getName());
            patientModel.setBirthday(patient.getBirthday());
            patientModel.setDemographicId(patient.getDemographicId());
            patientModel.setSex(patient.getSex());
            patientModel.setMaritalStatus(patient.getMaritalStatus());
            patientModel.setPhone(patient.getPhone());
            patientModel.setIsVerified(patient.getIsVerified());
        }
        return true;

    }

    public boolean deletePatientByID(String id){
        patientDao.deleteBydemographicId(id);
        return true;
    }

    public boolean addPatient(MrPatientsEntity patient){
        if(patient.getPhone()==null&&patient.getDemographicId()==null){
            return false;
        }
        else if(patientDao.findBydemographicId(patient.getDemographicId())!=null ||patientDao.findByphone(patient.getPhone())!=null){
            return false;
        }
        else {
            patientDao.save(patient);
            return true;
        }
    }
}
