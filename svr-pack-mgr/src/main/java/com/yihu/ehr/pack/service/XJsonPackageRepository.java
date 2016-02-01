package com.yihu.ehr.pack.service;

import com.yihu.ehr.constants.ArchiveStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
@Repository
public interface XJsonPackageRepository extends PagingAndSortingRepository<JsonPackage, String> {

    @Query("select count(*) from JsonPackage pack where pack.receiveDate between :since and :to")
    long count(@Param("since") Date since, @Param("to") Date to);

    @Query("select pack from JsonPackage pack where pack.receiveDate between :since and :to")
    List<JsonPackage> findAll(@Param("since") Date since, @Param("to") Date to);

    @Query("select pack from JsonPackage pack where archiveStatus in (:archiveStatus) and receiveDate between :since and :to")
    List<JsonPackage> findAll(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("since") Date since, @Param("to") Date to, Pageable pageable);

    @Query("select pack from JsonPackage pack where archiveStatus = :archiveStatus order by receiveDate asc")
    JsonPackage findEarliestOne();
}
