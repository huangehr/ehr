package com.yihu.ehr.org.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XOrganizationRepository extends PagingAndSortingRepository<Organization, String> {

    @Query("select org from Organization org where org.location in (:geographyIds)")
    List<Organization> searchByAddress(@Param("geographyIds") List<String> geographyIds);

    @Query("select org.orgCode from Organization org where org.fullName like %:name% or org.shortName like %:name%")
    List<String> fingIdsByFullnameOrShortName(@Param("name") String name);

    @Query("select org from Organization org where org.admin = :adminLoginCode and org.orgCode = :orgCode")
    List<Organization>findByOrgAdmin(@Param("orgCode")  String orgCode,@Param("adminLoginCode")  String adminLoginCode);
}
