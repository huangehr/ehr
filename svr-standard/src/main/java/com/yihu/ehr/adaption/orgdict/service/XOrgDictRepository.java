package com.yihu.ehr.adaption.orgdict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.01.19 13:44
 */
public interface XOrgDictRepository extends PagingAndSortingRepository<OrgDict, Long> {

    @Query("select orgDict from OrgDict orgDict where organization = :orgCode and (code = :code )")
    List<OrgDict> isExistOrgDict(@Param("orgCode") String orgCode, @Param("code") String code);

    @Query("select orgDict from OrgDict orgDict where organization = :org_code and sequence = :sequence ")
    OrgDict getOrgDictBySequence(@Param("org_code") String orgCode, @Param("sequence") int sequence);

}
