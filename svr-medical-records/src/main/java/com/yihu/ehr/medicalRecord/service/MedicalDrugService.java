package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.hbaseDao.MedicalDrugDao;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalDrug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shine on 2016/7/19.
 */
@Transactional
@Service
public class MedicalDrugService {

    @Autowired
    MedicalDrugDao medicalDrugDao;

    /**
     * 获取用药信息
     */
    public List<MedicalDrug> getMedicalDrug(String recordId) throws Exception{

        return null;
    }

    /**
     * 导入用药信息
     */
    public boolean importMedicalPrescription(List<MedicalDrug> list) throws Exception
    {
        return true;
    }

    /**
     * 保存病历用药记录
     */
    public boolean saveMedicalDrug(String recordId,MedicalDrug obj) throws Exception
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
