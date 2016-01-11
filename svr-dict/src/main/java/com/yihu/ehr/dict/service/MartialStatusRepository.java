package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface MartialStatusRepository extends PagingAndSortingRepository<MartialStatus, String> {
    @Query("select dict from MartialStatus dict where dict.dictId = 4 and dict.code = :code order by dict.sort asc")
    MartialStatus getMartialStatus(@Param("code") String code);
}
