package com.yihu.ehr.org.service;

import org.springframework.data.jpa.repository.Modifying;
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

    @Query("select org from Organization org where org.orgCode = :orgCode")
    public Organization getOrgByCode(@Param("orgCode") String orgCode);

    @Query("select org from Organization org where org.orgType = :type")
    public List<Organization> getOrgByOrgType(@Param("type") String type);

    @Query("select org from Organization org where org.location = (select org1.location from Organization org1 where org1.orgCode = :orgCode)")
    public List<Organization> getOrgByLocationWithCode(@Param("orgCode") String orgCode);




//    @Modifying(clearAutomatically = true)
//    @Query("update Organization org set org.activityFlag = :flag where org.orgCode = :orgCode")
//    void update(@Param("orgCode") String orgCode, @Param("flag") int flag);
}
