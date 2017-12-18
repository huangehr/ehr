package com.yihu.ehr.pack.dao;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.pack.service.DatasetPackage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author HZY
 * @version 1.0
 * @created 2017.06.26 15:10
 */
@Repository
public interface XDatasetPackageRepository extends PagingAndSortingRepository<DatasetPackage, String> {

    @Query("select pack from DatasetPackage pack where archiveStatus in (:archiveStatus) and receiveDate between :from and :to")
    List<DatasetPackage> findAll(@Param("archiveStatus") ArchiveStatus archiveStatus, @Param("from") Date from, @Param("to") Date to, Pageable pageable);

    @Query("select pack from DatasetPackage pack where archiveStatus = 0 order by receiveDate asc")
    List<DatasetPackage> findEarliestOne(Pageable pageable);
}
