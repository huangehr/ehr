package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface OrgTypeRepository extends PagingAndSortingRepository<OrgType, String> {
    @Query("select dict from OrgType dict where dict.dictId = 7 and dict.code = :code order by dict.sort asc")
    OrgType getOrgType(@Param("code") String code);
}
