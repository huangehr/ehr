package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.cache.CacheCommonBiz;
import com.yihu.ehr.redis.cache.dao.RedisCacheCategoryDao;
import com.yihu.ehr.redis.cache.dao.RedisCacheKeyMemoryDao;
import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    RedisCacheCategoryDao redisCacheCategoryDao;

    public RedisCacheKeyMemory findByCacheKey(String cacheKey) {
        return redisCacheKeyMemoryDao.findByCacheKey(cacheKey);
    }

    /**
     * 合计指定分类的占用内存大小
     *
     * @param keyPrefix
     * @return
     */
    public Long sumCategoryMemory(String keyPrefix) {
        Long mem = redisCacheKeyMemoryDao.sumCategoryMemory(keyPrefix);
        return mem == null ? 0 : mem;
    }

    /**
     * 合计未分类的占用内存大小
     *
     * @return
     */
    public Long sumNotCategoryMemory() {
        StringBuffer sql = new StringBuffer("SELECT SUM(size_in_bytes) AS sumSize FROM redis_cache_key_memory WHERE ");
        List<RedisCacheCategory> categoryList = (List<RedisCacheCategory>) redisCacheCategoryDao.findAll();
        for (int i = 0, len = categoryList.size(); i < len; i++) {
            String keyPrefix = CacheCommonBiz.makeKeyPrefix(categoryList.get(i).getCode());
            if (i != 0) sql.append(" AND ");
            sql.append(" cache_key NOT LIKE '" + keyPrefix + "%' ");
        }
        Map<String, Object> result = jdbcTemplate.queryForMap(sql.toString());
        Object memObj = result.get("sumSize");
        return memObj == null ? 0 : Long.parseLong(memObj.toString());
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