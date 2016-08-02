package com.yihu.ehr.medicalRecord.dao;


import com.yihu.ehr.medicalRecord.model.Entity.MrSystemDictEntryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface SystemDictEntryDao extends PagingAndSortingRepository<MrSystemDictEntryEntity,Integer> {

    MrSystemDictEntryEntity findById(Integer id);

    MrSystemDictEntryEntity findByDictCodeAndCode(String dictCode,String code);

    @Query(value = "select * from mr_system_dict_entry where PHONETIC_CODE like ?1 or name like ?1", nativeQuery = true)
    List<MrSystemDictEntryEntity> findByLike(String filter);

    List<MrSystemDictEntryEntity> findByDictCode(String dictCode);
    void deleteByid(Integer id);
}
