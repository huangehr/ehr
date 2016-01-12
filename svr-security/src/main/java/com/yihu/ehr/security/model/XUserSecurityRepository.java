package com.yihu.ehr.security.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.token.Token;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XUserSecurityRepository extends PagingAndSortingRepository<UserSecurity, Integer> {


    @Query("select UserSecurity from UserSecurity userSecurity where 1=1")
    List<Token> findTokenList();


}
