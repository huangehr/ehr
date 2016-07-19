package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalLabelDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class MedicalLabelService extends BaseRestEndPoint {

    @Autowired
    MedicalLabelDao medicalLabelDao;
    public List<MrMedicalLabelEntity> getMedicalLabelInformationByRecordId(String RecordId){
       return medicalLabelDao.findByrecordsId(Integer.parseInt(RecordId));
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

    public boolean addMedicalLabels(List<MrMedicalLabelEntity> MedicalLabels){
        if(MedicalLabels.size()>0) {
            for (int i = 0; i < MedicalLabels.size(); i++) {
                MedicalLabels.get(i).getLabel();
                if(medicalLabelDao.findByRecordsIdAndLabel(MedicalLabels.get(i).getRecordsId(), MedicalLabels.get(i).getLabel())==null
                        || medicalLabelDao.findByRecordsIdAndLabel(MedicalLabels.get(i).getRecordsId(), MedicalLabels.get(i).getLabel()).size()==0)
                    medicalLabelDao.save(MedicalLabels.get(i));
            }
            return true;
        }
        else
            return false;

    }

    public List<String> getMEDICALLABELsByLable(String...Lable){
        return  medicalLabelDao.findByLabel(Lable);
    }

}
