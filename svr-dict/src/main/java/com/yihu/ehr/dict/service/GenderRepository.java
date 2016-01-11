package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface GenderRepository extends PagingAndSortingRepository<Gender, String> {
    @Query("select dict from Gender dict where dict.dictId = 3 and dict.code = :code order by dict.sort asc")
    Gender getGender(@Param("code") String code);
}

