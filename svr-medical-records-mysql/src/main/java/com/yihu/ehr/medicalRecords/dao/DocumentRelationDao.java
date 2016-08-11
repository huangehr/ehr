package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentRelationEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/7/27.
 */
public interface DocumentRelationDao extends PagingAndSortingRepository<MrDocumentRelationEntity,Integer> {

    @Modifying
    @Query("delete from MrDocumentRelationEntity mr " +
            "where mr.ownerType = '0' " +
            "  and mr.ownerId =:reportId")
    void deleteByOwnerId(@Param("reportId") String reportId);

    @Query("delete from MrDocumentRelationEntity mr " +
            "where mr.ownerType = '0' " +
            "  and mr.ownerId in :ownerIds")
    void deleteByOwnerIds(@Param("ownerIds") String[] ownerIds);

    @Query("select mr " +
            " from MrDocumentRelationEntity mr " +
            "where mr.ownerType = '0' " +
            "  and mr.ownerId=:reportId")
    List<MrDocumentRelationEntity> findByOwnerId(@Param("reportId") String reportId);

}
