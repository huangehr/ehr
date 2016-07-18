package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MrDiagnosisDictDao;
import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class MrDiagnosisDictService {

    @Autowired
    MrDiagnosisDictDao mrDiagnosisDictDao;
    public boolean addMrDiagnosisDict(MrDiagnosisDictEntity mrDiagnosisDict){
        mrDiagnosisDictDao.save(mrDiagnosisDict);
        return true;
    }


    public boolean deleteMrDiagnosisDict( String code){
        mrDiagnosisDictDao.deleteBycode(code);
        return true;
    }

    public boolean searchMrDiagnosisDict(String code){
        mrDiagnosisDictDao.findBycode(code);
        return true;
    }

    public boolean updateMrDiagnosisDict(MrDiagnosisDictEntity mrDiagnosisDict){
        if(mrDiagnosisDict!=null){
            MrDiagnosisDictEntity m=mrDiagnosisDictDao.findBycode(mrDiagnosisDict.getCode());
            if(m!=null)
            {
                m.setName(mrDiagnosisDict.getName());
                m.setDescription(mrDiagnosisDict.getDescription());
                return true;
            }
            else
                return false;

        }
        else
            return false;
    }
}
