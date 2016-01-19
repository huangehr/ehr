package com.yihu.ehr.security.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XUserSecurityRepository extends PagingAndSortingRepository<UserSecurity, String> {

//    @Query("select userSecurity from UserSecurity userSecurity where userSecurity.dictId = 1 and dict.code = :code order by dict.sort asc")
//    UserSecurity getByUserKey(String userKeyId);
}
