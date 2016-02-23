package com.yihu.ehr.org.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XOrganizationRepository extends PagingAndSortingRepository<Organization, String> {

    @Query("select org from Organization org where org.location in (:geographyIds)")
    List<Organization> searchByAddress(@RequestParam List<String> geographyIds);

    @Query("select org.orgCode from Organization org where org.fullName like %:name% or org.shortName like %:name%")
    List<String> fingIdsByFullnameOrShortName(@Param("name") String name);
}
