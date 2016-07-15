package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MrSystemDictDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrSystemDictEntryDao;
import com.yihu.ehr.medicalRecord.model.MrSystemDictEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class MrSystemDictService {

    @Autowired
    MrSystemDictDao mrSystemDictDao;
    @Autowired
    MrSystemDictEntryDao mrSystemDictEntryDao;

    public boolean addMrSystemDictEntry(MrSystemDictEntity MrSystemDictEntry){
        mrSystemDictDao.save(MrSystemDictEntry);
        return true;
    }


    public boolean deleteMrSystemDictEntry( String dictCode){
        if(mrSystemDictEntryDao.findBydictCode(dictCode)!=null)
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

    public boolean updataMrSystemDictEntry(MrSystemDictEntity mrSystemDictEntity){
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
