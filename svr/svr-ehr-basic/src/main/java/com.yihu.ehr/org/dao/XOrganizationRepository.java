package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.Organization;
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

    @Query("select org from Organization org where org.location in (:geographyIds) and org.activityFlag=1")
    List<Organization> searchByAddress(@Param("geographyIds") List<String> geographyIds);

    @Query("select org.orgCode from Organization org where org.fullName like %:name% or org.shortName like %:name%")
    List<String> fingIdsByFullnameOrShortName(@Param("name") String name);

    @Query("select org from Organization org where org.admin = :adminLoginCode and org.orgCode = :orgCode")
    List<Organization>findByOrgAdmin(@Param("orgCode")  String orgCode,@Param("adminLoginCode")  String adminLoginCode);

    @Query("select org from Organization org where org.orgCode in (:orgCodes)")
    List<Organization> findByOrgCodes(@Param("orgCodes") List<String> orgCodes);

    @Query("select org from Organization org where cast(org.administrativeDivision as string) like ?1")
    List<Organization> findByArea(String area);

    @Query("select org from Organization org where org.activityFlag=1")
    List<Organization> findAllOrg();

    @Query("select org from Organization org where org.id=:orgId")
    List<Organization> findOrgById(@Param("orgId") long orgId);

    @Query("select org from Organization org where org.orgCode=:orgCode")
    List<Organization> findOrgByCode(@Param("orgCode") String orgCode);

    @Query("select org from Organization org where org.id=:orgId and org.parentHosId=:orgPId")
    List<Organization> checkSunOrg(@Param("orgPId") int orgPId,@Param("orgId")  long orgId);

}
