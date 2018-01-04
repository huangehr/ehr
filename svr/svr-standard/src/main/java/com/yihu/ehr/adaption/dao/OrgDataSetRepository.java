package com.yihu.ehr.adaption.dao;

import com.yihu.ehr.adaption.model.OrgDataSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.01.19 13:44
 */
public interface OrgDataSetRepository extends PagingAndSortingRepository<OrgDataSet, Long> {

    @Query("select dataset from OrgDataSet dataset where dataset.organization = :orgCode and dataset.code = :code")
    List<OrgDataSet> isExistOrgDataSet(@Param("orgCode") String orgCode, @Param("code") String code);

    @Query("select dataset from OrgDataSet dataset where dataset.organization = :org_code and dataset.sequence = :sequence")
    OrgDataSet getDataSetBySequence(@Param("org_code") String orgCode, @Param("sequence") int sequence);
}
