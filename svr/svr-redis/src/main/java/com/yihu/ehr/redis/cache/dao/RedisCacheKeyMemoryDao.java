package com.yihu.ehr.redis.cache.dao;

import com.yihu.ehr.redis.cache.entity.RedisCacheKeyMemory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 缓存Key值占用内存分析 DAO
 *
 * @author 张进军
 * @date 2017/12/6 14:58
 */
public interface RedisCacheKeyMemoryDao extends PagingAndSortingRepository<RedisCacheKeyMemory, Integer> {

    RedisCacheKeyMemory findByCacheKey(@Param("cacheKey") String cacheKey);

}
