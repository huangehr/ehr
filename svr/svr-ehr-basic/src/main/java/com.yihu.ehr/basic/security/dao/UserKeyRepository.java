package com.yihu.ehr.basic.security.dao;

import com.yihu.ehr.entity.security.UserKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface UserKeyRepository extends PagingAndSortingRepository<UserKey, String> {

    @Query("select userKey from UserKey userKey where userKey.user =:userId and userKey.keyType = 'Personal'")
    List<UserKey> findByUserId(@Param("userId") String userId);

    @Query("select userKey from UserKey userKey where userKey.org =:orgCode and userKey.keyType = 'Org'")
    List<UserKey> findByOrgCdode(@Param("orgCode") String orgCode);
}
