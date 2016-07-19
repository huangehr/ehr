package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorDraftDao;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorTemplateDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
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
public class DoctorDraftService {

    @Autowired
    DoctorDraftDao dDDao;

    public List<MrDoctorDraftEntity> getDraftByDoctorId(int doctorId, String type){

         return dDDao.findBydoctorId(doctorId, type);
    }

    public MrDoctorDraftEntity saveDoctorDraft(MrDoctorDraftEntity doctorDraft){

        return dDDao.save(doctorDraft);
    }

    public MrDoctorDraftEntity updateDraftBy(int id){

        MrDoctorDraftEntity Draft = dDDao.findByid(id);
        Draft.setUsageCount(Draft.getUsageCount() + 1);

        return dDDao.save(Draft);
    }
}