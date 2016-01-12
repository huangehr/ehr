package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by zqb on 2015/12/21.
 */
public interface StdSourceTypeRepository extends PagingAndSortingRepository<StdSourceType, String> {
    @Query("select dict from StdSourceType dict where dict.dictId = 22 and dict.code = :code order by dict.sort asc")
    StdSourceType getStdSourceType(@Param("code") String code);
}
