package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecord.service.MedicalLabelService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public class MedicalLabelController {
    @Autowired
    MedicalLabelService medicalLabelService;

    public boolean addMedicalLabels(MrMedicalLabelEntity[] MedicalLabels){
        return  medicalLabelService.addMedicalLabels(MedicalLabels);
    }

    public List<MrMedicalLabelEntity> getMedicalLabelInformationByID(String RecordId){
        return medicalLabelService.getMedicalLabelInformationByRecordId(RecordId);
    }
}
