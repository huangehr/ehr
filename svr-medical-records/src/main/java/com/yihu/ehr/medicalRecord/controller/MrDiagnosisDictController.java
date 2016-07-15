package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
import com.yihu.ehr.medicalRecord.service.MrDiagnosisDictService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class MrDiagnosisDictController {

    @Autowired
    MrDiagnosisDictService mrDiagnosisDictService;


    public boolean addMrDiagnosisDict(MrDiagnosisDictEntity mrDiagnosisDict){
        return mrDiagnosisDictService.addMrDiagnosisDict(mrDiagnosisDict);
    }

    public boolean deleteMrDiagnosisDict( String code){
        return mrDiagnosisDictService.deleteMrDiagnosisDict(code);
    }

    public boolean updataMrDiagnosisDict(MrDiagnosisDictEntity mrDiagnosisDict){
        return mrDiagnosisDictService.updataMrDiagnosisDict(mrDiagnosisDict);
    }
}
