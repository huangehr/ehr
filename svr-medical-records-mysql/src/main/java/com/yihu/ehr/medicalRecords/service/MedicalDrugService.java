package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalDrugEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hzp on 2016/7/19.
 */
@Transactional
@Service
public class MedicalDrugService {

    /*@Autowired
    MedicalDrugDao medicalDrugDao;*/

    /**
     * 获取用药信息
     */
    public List<MrMedicalDrugEntity> getMedicalDrug(String recordId) throws Exception{

        return null;
    }

    /**
     * 导入用药信息
     */
    public boolean importMedicalPrescription(List<MrMedicalDrugEntity> list) throws Exception
    {
        return true;
    }

    /**
     * 保存病历用药记录
     */
    public boolean saveMedicalDrug(String recordId,MrMedicalDrugEntity obj) throws Exception
    {
        return true;
    }

    /**
     * 删除病历用药记录
     */
    public boolean deleteMedicalDrug(String recordId,String drugId) throws Exception
    {
        return true;
    }

}
