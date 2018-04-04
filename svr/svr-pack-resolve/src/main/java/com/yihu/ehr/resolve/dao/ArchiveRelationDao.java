package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by progr1mmer on 2018/4/3.
 */
public interface ArchiveRelationDao extends PagingAndSortingRepository<ArchiveRelation, Long> {

    @Query("SELECT ar.idCardNo FROM ArchiveRelation ar WHERE ar.orgCode = :orgCode AND ar.cardNo = :cardNo")
    List<String> findByOrgCodeAndCardNo(@Param("orgCode") String orgCode, @Param("cardNo") String cardNo);

}
