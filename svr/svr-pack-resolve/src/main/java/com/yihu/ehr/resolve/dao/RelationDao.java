package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface RelationDao extends PagingAndSortingRepository<ArchiveRelation, Long> {

    ArchiveRelation findByProfileId(String profileId);

}
