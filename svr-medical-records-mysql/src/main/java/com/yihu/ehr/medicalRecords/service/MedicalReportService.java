package com.yihu.ehr.medicalRecords.service;


import com.yihu.ehr.medicalRecords.dao.hbaseDao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class MedicalReportService {

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    /**
     * 获取病历报告
     */
    public MedicalReportDTO getMedicalReport(String recordId,String reportId) throws Exception
    {
        return null;
    }

    /**
     * 保存病历报告
     */
    public boolean saveMedicalReport(String recordId,MedicalReportDTO obj) throws Exception
    {
        return true;
    }

    /**
     * 根据报告id删除病历报告
     */
    public boolean deleteReportById(String recordId,String reportId) throws Exception
    {
        return true;
    }
}