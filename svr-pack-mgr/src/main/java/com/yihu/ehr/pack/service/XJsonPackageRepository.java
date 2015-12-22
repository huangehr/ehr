package com.yihu.ehr.pack.service;

import com.yihu.ehr.constrant.ArchiveStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XJsonPackageRepository extends PagingAndSortingRepository<JsonPackage, String> {

    @Query("select count(*) from JsonPackage pack where pack.receiveDate between :from and :to")
    long count(@Param("from") Date from, @Param("to") Date to);

    @Query("select pack from JsonPackage pack where pack.receiveDate between :from and :to")
    List<JsonPackage> findAll(@Param("from") Date from, @Param("to") Date to);

    @Query("select pack from JsonPackage pack where archiveStatus in (:archiveStatus) and receiveDate between :from and :to")
    List<JsonPackage> findAll(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("from") Date from, @Param("to") Date to, Pageable pageable);

    @Query("select pack from JsonPackage pack where archiveStatus = :archiveStatus order by receiveDate asc")
    JsonPackage findEarliestOne();
}
