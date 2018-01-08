package com.yihu.ehr.redisMemory.cache.dao;

import com.yihu.ehr.redisMemory.cache.entity.RedisCacheKeyMemory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 缓存Key值占用内存分析 DAO
 *
 * @author 张进军
 * @date 2017/12/6 14:58
 */
public interface RedisCacheKeyMemoryDao extends JpaRepository<RedisCacheKeyMemory, Integer> {

}
