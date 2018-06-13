package com.yihu.ehr.basic.org.dao;

import com.yihu.ehr.basic.org.model.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, String> {

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

    @Query("select org from Organization org where org.fullName like %:name% or org.shortName like %:name%")
    List<Organization> fingorgByFullnameOrShortName(@Param("name") String name);

    @Query("select org.id from Organization org where org.orgCode = :orgCode")
    List<Long> getOrgIdByOrgCode(@Param("orgCode") String orgCode);

    @Query("select org.orgCode from Organization org where org.id = :orgId")
    String findOrgCodeByOrgId(@Param("orgId") Long orgId);

    @Query("select org.orgCode from Organization org where org.fullName in (:fullName)")
    List<String> findOrgCodeByFullName(@Param("fullName") List<String> fullName);

    @Query("select org.id from Organization org where org.orgCode  in (:orgCode) ")
    List<Long> findOrgIdByOrgCodeList(@Param("orgCode") List<String> orgCode);

    @Query("select org.orgCode from Organization org where org.id in(:orgId)")
    List<String> findOrgListById(@Param("orgId") List<Long> orgId);

    @Query("select org from Organization org where org.id in (:orgId)")
    List<Organization> findOrgByIds(@Param("orgId") long[] orgId);

    @Query("select org.id from Organization org where org.orgCode in(:orgCode)")
    List<String> getIdByOrgCode(@Param("orgCode") List<String> orgCode);

    @Query("select org from Organization org where org.orgType='Hospital' and org.ing !='' and  org.ing is not null and org.lat!='' and org.lat is not null" )
    List<Organization> getHospital();

    @Query(value = "SELECT org.* from Organizations org WHERE org.administrative_division in(SELECT id from address_dict where pid = :pid)", nativeQuery = true)
    List<Organization> getOrgListByAddressPid(@Param("pid") Integer pid);

    @Query(value = "SELECT org.* from Organizations org WHERE org.administrative_division in(SELECT id from address_dict where pid = :pid) and org.full_name like %:fullName%", nativeQuery = true)
    List<Organization> getOrgListByAddressPidAndParam(@Param("pid") Integer pid, @Param("fullName") String fullName);
}
