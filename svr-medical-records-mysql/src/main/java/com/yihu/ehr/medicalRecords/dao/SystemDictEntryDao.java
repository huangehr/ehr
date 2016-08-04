package com.yihu.ehr.medicalRecords.dao;


import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
public interface SystemDictEntryDao extends PagingAndSortingRepository<MrSystemDictEntryEntity,Integer> {


    MrSystemDictEntryEntity findByDictCodeAndCode(String dictCode, String code);

    List<MrSystemDictEntryEntity> findByDictCode(String dictCode);

    void deleteByid(Integer id);
}
