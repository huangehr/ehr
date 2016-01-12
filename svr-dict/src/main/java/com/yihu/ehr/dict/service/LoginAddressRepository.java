package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by wq on 2015/10/20.
 */
public interface LoginAddressRepository extends PagingAndSortingRepository<LoginAddress, String> {
    @Query("select dict from LoginAddress dict where dict.dictId = 20 and dict.code = :code order by dict.sort asc")
    LoginAddress getLoginAddress(@Param("code") String code);
}
