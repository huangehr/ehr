package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorDraftDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalDraftDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalDraftEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class MedicalDraftService {

    @Autowired
    MedicalDraftDao mDDao;

    public List<MrMedicalDraftEntity> getDraftByDoctorId(int doctorId, String type){

         return  mDDao.findBydoctorIdAndType(doctorId, type);
    }
}