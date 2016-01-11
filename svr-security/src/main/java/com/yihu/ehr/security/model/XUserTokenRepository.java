package com.yihu.ehr.security.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.token.Token;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XUserTokenRepository extends PagingAndSortingRepository<UserToken, Integer> {


    @Query("select userToken from UserToken UserToken where 1=1")
    List<Token> findTokenList();


}
