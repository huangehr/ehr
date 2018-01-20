package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by hzp on 2017/4/5.
 */
public interface XArchiveRelationDao extends PagingAndSortingRepository<ArchiveRelation,Long> {

    Page<ArchiveRelation> findByIdCardNo(String idCardNo, Pageable pageable);

    Page<ArchiveRelation> findByCardNo(String cardNo, Pageable pageable);

    @Query("select a from ArchiveRelation a where a.status=0 and a.cardNo=:cardNo and a.name = :name")
    List<ArchiveRelation> findByCardNoAndName(@Param("cardNo") String cardNo, @Param("name") String name);

    ArchiveRelation findByApplyId(long applyId);

    ArchiveRelation findByProfileId(String profileId);

    @Query("select count(ar.id) from ArchiveRelation ar where ar.identifyFlag = 1")
    int findIdentifyCount();

    @Query("select count(ar.id) from ArchiveRelation ar where ar.identifyFlag = 0")
    int findUnIdentifyCount();

    @Query("select count(ar.id) from ArchiveRelation ar")
    int findArchiveCount();

    @Query("select count(ar.id) from ArchiveRelation ar where ar.eventType = 0")
    int findOutPatientCount();

    @Query("select count(ar.id) from ArchiveRelation ar where ar.eventType = 1")
    int findInPatientCount();

    @Query("select count(ar.id) from ArchiveRelation ar where ar.eventType = 2")
    int findPhysicalCount();

    @Query("select count(ar.id) from ArchiveRelation ar where ar.eventType = 0 or ar.eventType = 1")
    int findInAndOutPatientCount();

    //今日入库
    @Query("select count(ar.id) from ArchiveRelation ar where ar.createDate >= :curDate")
    int findTodayInWarehouseCount(@Param("curDate") Date curDate);

    //累计就诊人次--每日新增
    @Query("select count(ar.id) from ArchiveRelation ar where ar.identifyFlag = 1 and ar.eventDate >= :curDate")
    int FindDailyAdd(@Param("curDate") Date curDate);
}
