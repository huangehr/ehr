package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.medicalRecord.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class PatientController {

    @Autowired
    PatientService patientService;

    public boolean addPatient(MrPatientsEntity patient){
        return patientService.addPatient(patient);
    }

    public MrPatientsEntity getPatientInformationBydemographicIdOrPhone(String demographicId,String phone){
        return patientService.getPatientInformationBydemographicIdOrPhone(demographicId,phone);
    }

}
