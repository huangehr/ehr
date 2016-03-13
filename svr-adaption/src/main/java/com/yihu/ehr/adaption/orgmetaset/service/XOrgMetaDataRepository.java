package com.yihu.ehr.adaption.orgmetaset.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.01.19 13:44
 */
public interface XOrgMetaDataRepository extends PagingAndSortingRepository<OrgMetaData, Long> {

    @Query("select metadata from OrgMetaData metadata where orgDataSet= :orgDataSetSeq and organization=:orgCode  and code = :code")
    List<OrgMetaData> isExistOrgMetaData(@Param("orgDataSetSeq")int orgDataSetSeq, @Param("orgCode")String orgCode, @Param("code")String code);

}
