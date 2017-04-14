package com.yihu.ehr.patient.dao;

import com.yihu.ehr.model.patient.ArchiveRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XArchiveRelationDao extends PagingAndSortingRepository<ArchiveRelation,Long> {

    Page<ArchiveRelation> findByIdCardNo(String idCardNo, Pageable pageable);

    Page<ArchiveRelation> findByCardNo(String cardNo, Pageable pageable);

    @Query("select a from ArchiveRelation a where a.status=0 and a.cardNo=:cardNo and a.name = :name")
    List<ArchiveRelation> findByCardNoAndName(String cardNo, String name);

}
