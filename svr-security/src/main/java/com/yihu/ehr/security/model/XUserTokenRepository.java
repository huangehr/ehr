package com.yihu.ehr.security.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XUserTokenRepository extends PagingAndSortingRepository<UserToken, String> {


}
