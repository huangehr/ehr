package com.yihu.ehr.patient.dao;

import com.yihu.ehr.model.patient.ArchiveApply;
import com.yihu.ehr.model.patient.ArchiveRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XArchiveRelationDao extends PagingAndSortingRepository<ArchiveRelation,Long> {

    Page<ArchiveRelation> findByIdCardNo(String idCardNo, Pageable pageable);

    Page<ArchiveRelation> findByCardNo(String cardNo, Pageable pageable);

}
