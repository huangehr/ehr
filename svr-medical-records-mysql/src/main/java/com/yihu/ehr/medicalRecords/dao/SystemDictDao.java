package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2016/7/14.
 */
public interface SystemDictDao extends PagingAndSortingRepository<MrSystemDictEntity,String> {

    MrSystemDictEntity findBydictCode(String dictCode);

    void deleteBydictCode(String dictCode);
}
