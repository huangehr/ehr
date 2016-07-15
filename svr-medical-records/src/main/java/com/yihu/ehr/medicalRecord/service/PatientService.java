package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class PatientService {


    @Autowired
    PatientDao patientDao;
    public MrPatientsEntity getPatientInformationBydemographicIdOrPhone(String demographicId,String phone){
        if(demographicId!=null&&patientDao.findBydemographicId(demographicId)==null){
            if(phone!=null&&patientDao.findByphone(phone)==null)
                return null;
            else
                return patientDao.findByphone(phone);
        }
        else
            return patientDao.findBydemographicId(demographicId);
    }

    public boolean updataPatientInformationByID(MrPatientsEntity patient){

        MrPatientsEntity patientModel=patientDao.findBydemographicId(String.valueOf(patient.getId()));
        if(patient!=null) {
            patientModel = patient;
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
        else {
            patientDao.save(patient);
            return true;
        }
    }
}
