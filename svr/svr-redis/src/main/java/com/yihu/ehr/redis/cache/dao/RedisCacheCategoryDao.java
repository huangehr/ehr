package com.yihu.ehr.redis.cache.dao;

import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 缓存分类 DAO
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
public interface RedisCacheCategoryDao extends PagingAndSortingRepository<RedisCacheCategory, Integer> {

    @Query(" FROM RedisCacheCategory a WHERE a.id <> :id AND a.name = :name ")
    RedisCacheCategory isUniqueName(@Param("id") Integer id, @Param("name") String name);

    @Query(" FROM RedisCacheCategory a WHERE a.id <> :id AND a.code = :code ")
    RedisCacheCategory isUniqueCode(@Param("id") Integer id, @Param("code") String code);

}
