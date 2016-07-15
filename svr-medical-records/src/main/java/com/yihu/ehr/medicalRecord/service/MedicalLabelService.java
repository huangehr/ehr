package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MedicalLabelDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public class MedicalLabelService {

    @Autowired
    MedicalLabelDao medicalLabelDao;
    public List<MrMedicalLabelEntity> getMedicalLabelInformationByRecordId(String RecordId){
       return medicalLabelDao.findByrecordsId(RecordId);
    }

    public boolean updataMEDICALLABELInformationByID(MrMedicalLabelEntity MedicalLabel){
        MrMedicalLabelEntity medicalLabelModel=medicalLabelDao.findByid(String.valueOf(MedicalLabel.getId()));
        if(MedicalLabel!=null) {
            medicalLabelModel = MedicalLabel;
        }
        return true;

    }

    public boolean deleteMedicalLabelById(String id){
        medicalLabelDao.deleteByid(id);
        return true;
    }

    public boolean addMedicalLabels(MrMedicalLabelEntity[] MedicalLabels){
        for(int i=0;i<MedicalLabels.length;i++) {
            medicalLabelDao.save(MedicalLabels[i]);
        }
        return true;
    }

    public List<String> getMEDICALLABELsByLable(String...Lable){
        return  medicalLabelDao.findByLabel(Lable);
    }

}
