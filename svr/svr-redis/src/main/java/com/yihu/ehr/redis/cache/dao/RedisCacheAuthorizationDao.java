package com.yihu.ehr.redis.cache.dao;

import com.yihu.ehr.redis.cache.entity.RedisCacheAuthorization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 缓存授权 DAO
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
public interface RedisCacheAuthorizationDao extends PagingAndSortingRepository<RedisCacheAuthorization, Integer> {

    RedisCacheAuthorization findByCategoryCodeAndAppId(@Param("categoryCode") String categoryCode, @Param("appId") String appId);

    @Query(" FROM RedisCacheAuthorization a WHERE a.id <> :id AND a.categoryCode = :categoryCode AND a.appId = :appId ")
    RedisCacheAuthorization isUniqueAppId(@Param("id") Integer id, @Param("categoryCode") String categoryCode, @Param("appId") String appId);

}
