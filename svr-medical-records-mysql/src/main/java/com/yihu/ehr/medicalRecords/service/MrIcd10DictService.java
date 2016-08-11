package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.Icd10DictDao;
import com.yihu.ehr.medicalRecords.dao.SystemDictEntryDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrIcd10DictEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
@Service
public class MrIcd10DictService {
    @Autowired
    Icd10DictDao icd10DictDao;


    /**
     * 获取icd10字典列表
     */
    public List<MrIcd10DictEntity> getIcd10List(String filter,Integer page,Integer size){

        return icd10DictDao.findByFilter(filter,new PageRequest(page-1, size));
    }
}
