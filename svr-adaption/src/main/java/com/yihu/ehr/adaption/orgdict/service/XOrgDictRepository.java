package com.yihu.ehr.adaption.orgdict.service;

import com.yihu.ehr.adaption.orgdataset.service.OrgDataSet;
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
    List<OrgDataSet> isExistOrgDict(@Param("orgCode") String orgCode, @Param("code") String code);

}
