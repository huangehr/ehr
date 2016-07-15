package com.yihu.ehr.medicalRecord.dao.intf;


import com.yihu.ehr.medicalRecord.model.MrSystemDictEntryEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MrSystemDictEntryDao extends PagingAndSortingRepository<MrSystemDictEntryEntity,Integer> {

    MrSystemDictEntryEntity findById(Integer id);

    MrSystemDictEntryEntity findByDictCodeAndCode(String dictCode,String code);

    List<MrSystemDictEntryEntity> findByDictCode(String dictCode);
    void deleteByid(Integer id);
}
