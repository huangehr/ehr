package com.yihu.ehr.security.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XKeyMapRepository extends PagingAndSortingRepository<KeyMap, String> {

    @Query("select userKey from KeyMap userKey where userKey.user =:userId and userKey.keyType = 'Personal'")
    List<KeyMap> findByUserId(@Param("userId") String userId);

    @Query("select userKey from KeyMap userKey where userKey.org =:orgCode and userKey.keyType = 'Org'")
    List<KeyMap> findByOrgCdode(@Param("orgCode") String orgCode);
}
