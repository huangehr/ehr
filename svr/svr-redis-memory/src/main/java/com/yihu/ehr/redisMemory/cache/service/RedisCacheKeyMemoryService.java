package com.yihu.ehr.redisMemory.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redisMemory.cache.dao.RedisCacheKeyMemoryDao;
import com.yihu.ehr.redisMemory.cache.entity.RedisCacheKeyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = false)
    public Iterable<RedisCacheKeyMemory> save(List<RedisCacheKeyMemory> list) {
        return redisCacheKeyMemoryDao.save(list);
    }

    @Transactional(readOnly = false)
    public void deleteAllInBatch() {
        redisCacheKeyMemoryDao.deleteAllInBatch();
    }

}