package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrIcd10DictEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
public interface Icd10DictDao extends PagingAndSortingRepository<MrIcd10DictEntity,String> {

    @Query("select m from MrIcd10DictEntity m where m.name like '%:filter%'")
    List<MrIcd10DictEntity> findByFilter(@Param("filter") String filter,
                                         Pageable pageable);
}
