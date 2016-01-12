package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface RequestStateRepository extends PagingAndSortingRepository<RequestState, String> {
    @Query("select dict from RequestState dict where dict.dictId = 11 and dict.code = :code order by dict.sort asc")
    RequestState getRequestState(@Param("code") String code);
}
