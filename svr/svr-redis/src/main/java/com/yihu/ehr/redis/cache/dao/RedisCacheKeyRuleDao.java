package com.yihu.ehr.redis.cache.dao;

import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 缓存Key生成规则 DAO
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
public interface RedisCacheKeyRuleDao extends PagingAndSortingRepository<RedisCacheKeyRule, Integer> {

    RedisCacheKeyRule findByCode(@Param("code") String code);

    @Query(" FROM RedisCacheKeyRule a WHERE a.id <> :id AND a.name = :name ")
    RedisCacheKeyRule isUniqueName(@Param("id") Integer id, @Param("name") String name);

    @Query(" FROM RedisCacheKeyRule a WHERE a.id <> :id AND a.code = :code ")
    RedisCacheKeyRule isUniqueCode(@Param("id") Integer id, @Param("code") String code);

}
