package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.SystemDictDao;
import com.yihu.ehr.medicalRecords.dao.SystemDictEntryDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hzp on 2016/7/14.
 */
@Transactional
@Service
public class MrSystemDictService {

    @Autowired
    SystemDictDao mrSystemDictDao;
    @Autowired
    SystemDictEntryDao mrSystemDictEntryDao;

    public boolean addMrSystemDict(MrSystemDictEntity MrSystemDict){
        mrSystemDictDao.save(MrSystemDict);
        return true;
    }


    public boolean deleteMrSystemDict( String dictCode){
        if(mrSystemDictEntryDao.findByDictCode(dictCode)!=null&&mrSystemDictEntryDao.findByDictCode(dictCode).size()>0)//系统字典项无值才可删除
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
