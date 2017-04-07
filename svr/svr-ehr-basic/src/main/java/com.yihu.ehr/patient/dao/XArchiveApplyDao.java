package com.yihu.ehr.patient.dao;

import com.yihu.ehr.model.patient.ArchiveApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XArchiveApplyDao extends PagingAndSortingRepository<ArchiveApply,Long> {

    Page<ArchiveApply> findByUserId(String userId, Pageable pageable);

    Page<ArchiveApply> findByUserIdAndStatus(String userId, String status, Pageable pageable);

    Page<ArchiveApply> findByStatus(String status, Pageable pageable);


}
