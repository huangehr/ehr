package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.MedicalDrugDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalDrugEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hzp on 2016/7/19.
 */
@Service
public class MedicalDrugService {

    @Autowired
    MedicalDrugDao medicalDrugDao;

    /**
     * 获取用药信息
     */
    public List<MrMedicalDrugEntity> getMedicalDrug(String recordId) throws Exception{
        return medicalDrugDao.findByRecordId(recordId);
    }

    /**
     * 导入用药信息(覆盖)
     */
    @Transactional
    public boolean importMedicalPrescription(String recordId,List<MrMedicalDrugEntity> list) throws Exception
    {
        List<MrMedicalDrugEntity> oldList = medicalDrugDao.findByRecordId(recordId);
        //清空数据
        medicalDrugDao.delete(oldList);
        medicalDrugDao.save(list);
        return true;
    }

    /**
     * 保存病历用药记录
     */
    public boolean saveMedicalDrug(String recordId,MrMedicalDrugEntity obj) throws Exception
    {
        medicalDrugDao.save(obj);
        return true;
    }

    /**
     * 删除病历用药记录
     */
    public boolean deleteMedicalDrug(String recordId,String drugId) throws Exception
    {
        medicalDrugDao.delete(Integer.valueOf(drugId));
        return true;
    }

    /**
     * 删除病历用药记录
     */
    public boolean deleteMedicalDrug(String recordId) throws Exception
    {
        medicalDrugDao.deleteByRecordId(recordId);
        return true;
    }
}
