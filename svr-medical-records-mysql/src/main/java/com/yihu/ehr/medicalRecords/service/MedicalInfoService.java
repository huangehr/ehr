package com.yihu.ehr.medicalRecords.service;


import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalInfoDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalInfoEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */

@Service
public class MedicalInfoService {

    @Autowired
    MedicalInfoDao medicalInfoDao;

    /**
     * 保存病情
     */
    @Transactional
    public boolean saveMedicalInfo(String recordId, List<MrMedicalInfoEntity> list) throws Exception {
        List<MrMedicalInfoEntity> oldList = medicalInfoDao.findByRecordId(recordId);
        //清空数据
        medicalInfoDao.delete(oldList);
        medicalInfoDao.save(list);
        return true;
    }

    /**
     * 删除病情
     */
    @Transactional
    public boolean deleteMedicalInfo(String recordId) throws Exception {

        medicalInfoDao.deleteByRecordId(recordId);
        return true;
    }

    /**
     * 获取病情
     */
    public List<MrMedicalInfoEntity> getMedicalInfo(String recordId) throws Exception {
        return medicalInfoDao.findByRecordId(recordId);
    }


}