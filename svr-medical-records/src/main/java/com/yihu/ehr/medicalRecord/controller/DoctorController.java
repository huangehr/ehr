package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class DoctorController {


    @Autowired
    DoctorService doctorService;
    public MrDoctorsEntity getDoctorInformationByDemographicId(String demographicId){
       return doctorService.getDoctorInformationByDemographicId(demographicId);
    }

    public boolean addDoctor(MrDoctorsEntity doctor){
        return doctorService.addDoctor(doctor);
    }

    public boolean updataDoctorStatusByDemographicId(String status,String demographicId){
        return doctorService.updataDoctorStatusByDemographicId(status, demographicId);
    }

    public boolean updataDoctorInformationByDemographicId(MrDoctorsEntity doctor) {
        return doctorService.updataDoctorInformationByDemographicId(doctor);
    }
}
