package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface UserTypeRepository extends PagingAndSortingRepository<UserType, String> {
    @Query("select dict from UserType dict where dict.dictId = 15 and dict.code = :code order by dict.sort asc")
    UserType getUserType(@Param("code") String code);

    @Query("select dict from UserType dict where dict.dictId = 15 order by dict.sort asc")
    List<UserType> getUserTypeList();
}

