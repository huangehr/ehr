package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.cache.dao.RedisCacheKeyMemoryDao;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缓存Key值占用内存分析 Service
 *
 * @author 张进军
 * @date 2017/12/6 14:58
 */
@Service
@Transactional
public class RedisCacheKeyMemoryService extends BaseJpaService<RedisCacheKeyMemory, RedisCacheKeyMemoryDao> {

    @Autowired
    RedisCacheKeyMemoryDao redisCacheKeyMemoryDao;

    public RedisCacheKeyMemory findByCacheKey(String cacheKey) {
        return redisCacheKeyMemoryDao.findByCacheKey(cacheKey);
    }

    @Transactional(readOnly = false)
    public RedisCacheKeyMemory save(RedisCacheKeyMemory redisCacheKeyMemory) {
        return redisCacheKeyMemoryDao.save(redisCacheKeyMemory);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisCacheKeyMemoryDao.delete(id);
    }

    @Transactional(readOnly = false)
    public void deleteAll() {
        redisCacheKeyMemoryDao.deleteAll();
    }

}