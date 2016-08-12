package com.yihu.ehr.medicalRecords.service;


import com.yihu.ehr.medicalRecords.dao.DoctorTemplateDao;
import com.yihu.ehr.medicalRecords.dao.MedicalInfoDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorTemplateEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */

@Service
public class MedicalInfoService {

    @Autowired
    MedicalInfoDao medicalInfoDao;

    @Autowired
    DoctorTemplateDao dTDao;

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
     * 保存病情单个
     */
    public boolean saveTemplate(MrMedicalInfoEntity entity,String doctorId){
        //医生病情模板存在否？新增
        List<MrDoctorTemplateEntity> template = dTDao.findByDoctorIdAndName(doctorId, entity.getName());
        if(template == null || template.size() ==0){
            MrDoctorTemplateEntity templateEntity = new MrDoctorTemplateEntity();
            templateEntity.setDoctorId(doctorId);
            templateEntity.setCode(entity.getCode());
            templateEntity.setName(entity.getName());
            dTDao.save(templateEntity);
        }
        List<MrMedicalInfoEntity> entityOld = medicalInfoDao.findByRecordIdAndName(entity.getRecordId(), entity.getName());
        if(entityOld != null && entityOld.size()>0){
            return true;
        }
        medicalInfoDao.save(entity);
        return true;
    }
    /**
     * 删除病情单个
     */
    public boolean deleteById(Integer id){
        medicalInfoDao.delete(id);
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