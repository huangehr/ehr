package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.MedicalReportDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class MedicalReportService {

    @Autowired
    MedicalReportDao medicalReportDao;

    /**
     * 创建数据元
     *
     * @param medicalReport MrMedicalReportEntity 数据元
     * @return MrMedicalReportEntity 数据元
     */
    public MrMedicalReportEntity saveMedicalRecord(MrMedicalReportEntity medicalReport){

        return medicalReportDao.save(medicalReport);
    }

    public List<MrMedicalReportEntity> getMedicalReportInfoByRecordId(int RecordId){

         return  medicalReportDao.findByrecordsId(RecordId);
    }

    /**
     * 删除数据元
     *
     * @param id int 数据元ID
     */
    public void deleteRecord(int id)
    {

        MrMedicalReportEntity medicalRecord = medicalReportDao.findByid(id);
        medicalReportDao.delete(medicalRecord);
    }
}