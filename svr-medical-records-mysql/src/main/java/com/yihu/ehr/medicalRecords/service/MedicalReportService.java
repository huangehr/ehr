package com.yihu.ehr.medicalRecords.service;


import com.yihu.ehr.medicalRecords.model.DTO.MedicalReportDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalReportEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hzp on 2016/8/4.
 * 辅助检查报告
 */
@Service
@Transactional
public class MedicalReportService {

    //@Autowired
    //MedicalRecordsDao medicalRecordsDao;

    /**
     * 获取辅助检查报告
     */
    public MedicalReportDTO getMedicalReport(String recordId, String reportId) throws Exception
    {
        return null;
    }

    /**
     * 保存辅助检查报告
     */
    public boolean saveMedicalReport(String recordId,MrMedicalReportEntity obj) throws Exception
    {
        return true;
    }

    /**
     * 根据报告id删除辅助检查报告
     */
    public boolean deleteReportById(String recordId,String reportId) throws Exception
    {
        return true;
    }
}