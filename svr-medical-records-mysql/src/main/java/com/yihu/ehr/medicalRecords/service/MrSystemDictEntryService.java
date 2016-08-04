package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.SystemDictEntryDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
@Transactional
@Service
public class MrSystemDictEntryService {
    @Autowired
    SystemDictEntryDao mrSystemDictEntryDao;


    public boolean addMrSystemDictEntry(MrSystemDictEntryEntity MrSystemDictEntry){
        mrSystemDictEntryDao.save(MrSystemDictEntry);
        return true;
    }


    public boolean deleteMrSystemDictEntry( String DictCode,String Code){
        mrSystemDictEntryDao.deleteByid(mrSystemDictEntryDao.findByDictCodeAndCode(DictCode,Code).getId());
        return true;
    }

    public List<MrSystemDictEntryEntity> searchSystemDictEntry(String filter)throws Exception {
        return mrSystemDictEntryDao.findByLike(filter);
    }

    public boolean searchMrSystemDictEntry(Integer id){
        mrSystemDictEntryDao.findById(id);
        return true;
    }

    public boolean updataMrSystemDictEntry(MrSystemDictEntryEntity mrSystemDictEntry){
        if(mrSystemDictEntry!=null){
            MrSystemDictEntryEntity m=mrSystemDictEntryDao.findByDictCodeAndCode(mrSystemDictEntry.getDictCode(), mrSystemDictEntry.getCode());
            if(m!=null)
            {
                m.setName(mrSystemDictEntry.getName());
                m.setDescription(mrSystemDictEntry.getDescription());
                return true;
            }
            else
                return false;

        }
        else
            return false;
    }
}
