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
public interface XPackageRepository extends PagingAndSortingRepository<Package, String> {

    @Query("select pack from Package pack where archiveStatus in (:archiveStatus) and receiveDate between :from and :to")
    List<Package> findAll(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("from") Date from, @Param("to") Date to, Pageable pageable);

    @Query("select pack from Package pack where archiveStatus = 0 order by receiveDate asc")
    List<Package> findEarliestOne(Pageable pageable);
}
