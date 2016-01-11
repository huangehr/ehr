package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface AdapterTypeRepository extends PagingAndSortingRepository<AdapterType, String> {
    @Query("select dict from AdapterType dict where dict.dictId = 21 and dict.code = :code order by dict.sort asc")
    AdapterType getAdapterType(@Param("code") String code);
}

