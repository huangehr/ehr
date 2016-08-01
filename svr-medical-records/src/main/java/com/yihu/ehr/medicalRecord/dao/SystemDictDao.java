package com.yihu.ehr.medicalRecord.dao;

import com.yihu.ehr.medicalRecord.model.Entity.MrSystemDictEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface SystemDictDao extends PagingAndSortingRepository<MrSystemDictEntity,String> {

    MrSystemDictEntity findBydictCode(String dictCode);

    void deleteBydictCode(String dictCode);
}
