package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorTemplateDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorTemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class DoctorTemplateService {

    @Autowired
    DoctorTemplateDao dTDao;

    public List<MrDoctorTemplateEntity> getTemplateByDoctorId(int doctorId){

         return  dTDao.findBydoctorId(doctorId);
    }
}