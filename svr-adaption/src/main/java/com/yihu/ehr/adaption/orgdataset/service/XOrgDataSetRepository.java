package com.yihu.ehr.adaption.orgdataset.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.01.19 13:44
 */
public interface XOrgDataSetRepository extends PagingAndSortingRepository<OrgDataSet, Long> {

    @Query("select dataset from OrgDataSet dataset where organization = :orgCode and code = :code")
    List<OrgDataSet> isExistOrgDataSet(String orgCode, String code);

}
