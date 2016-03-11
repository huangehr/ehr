package com.yihu.ehr.adaption.orgdictitem.service;

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
public interface XOrgDictItemRepository extends PagingAndSortingRepository<OrgDictItem, Long> {

    @Query("select orgDictItem from OrgDictItem orgDictItem where orgDict =:orgDictSeq and organization = :orgCode and code = :code")
    List<OrgDataSet> isExistOrgDictItem(
            @Param("orgDictSeq")long orgDictSeq,
            @Param("orgCode")String orgCode,
            @Param("code")String code);

}
