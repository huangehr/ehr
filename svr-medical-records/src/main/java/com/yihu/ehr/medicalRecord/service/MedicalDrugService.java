package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MedicalDrugDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalDrugEntity;
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

    public List<MrMedicalDrugEntity> getMedicalDrugInformationByRecordsId(int recordsId){

        return medicalDrugDao.findByrecordsId(recordsId);
    }


    public boolean postMedicalDrug(List<MrMedicalDrugEntity> mrMedicalDrugEntities) {

        if (mrMedicalDrugEntities != null && mrMedicalDrugEntities.size() > 0) {
            if (medicalDrugDao.findByrecordsId(mrMedicalDrugEntities.get(0).getRecordsId()) !=null) {
                medicalDrugDao.deleteByRecordsId(mrMedicalDrugEntities.get(0).getRecordsId());
            }
            for (int i = 0; i < mrMedicalDrugEntities.size(); i++) {
                medicalDrugDao.save(mrMedicalDrugEntities.get(i));
            }
        }
        return true;
    }
}
