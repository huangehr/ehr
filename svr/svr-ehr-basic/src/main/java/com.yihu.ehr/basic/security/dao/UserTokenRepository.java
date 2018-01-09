package com.yihu.ehr.basic.security.dao;

import com.yihu.ehr.entity.security.UserToken;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface UserTokenRepository extends PagingAndSortingRepository<UserToken, String> {


}
