package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface ResidenceTypeRepository extends PagingAndSortingRepository<ResidenceType, String> {
    @Query("select dict from ResidenceType dict where dict.dictId = 6 and dict.code = :code order by dict.sort asc")
    ResidenceType getResidenceType(@Param("code") String code);
}

