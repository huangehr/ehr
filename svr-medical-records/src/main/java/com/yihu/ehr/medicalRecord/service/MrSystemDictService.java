package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MrSystemDictDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrSystemDictEntryDao;
import com.yihu.ehr.medicalRecord.model.MrSystemDictEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class MrSystemDictService {

    @Autowired
    MrSystemDictDao mrSystemDictDao;
    @Autowired
    MrSystemDictEntryDao mrSystemDictEntryDao;

    public boolean addMrSystemDict(MrSystemDictEntity MrSystemDict){
        mrSystemDictDao.save(MrSystemDict);
        return true;
    }


    public boolean deleteMrSystemDict( String dictCode){
        if(mrSystemDictEntryDao.findByDictCode(dictCode)!=null)
            return false;
        else {
            mrSystemDictDao.deleteBydictCode(dictCode);
            return true;
        }
    }

    public boolean searchMrSystemDictEntry(String dictCode){
        mrSystemDictDao.findBydictCode(dictCode);
        return true;
    }

    public boolean updateMrSystemDict(MrSystemDictEntity mrSystemDictEntity){
        if(mrSystemDictEntity!=null){
            MrSystemDictEntity m=mrSystemDictDao.findBydictCode(mrSystemDictEntity.getDictCode());
            if(m!=null)
            {
                m.setDictName(mrSystemDictEntity.getDictName());
                m.setDescription(mrSystemDictEntity.getDescription());
                return true;
            }
            else
                return false;

        }
        else
            return false;
    }
}
