package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.cache.dao.RedisCacheCategoryDao;
import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缓存分类 Service
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@Service
@Transactional
public class RedisCacheCategoryService extends BaseJpaService<RedisCacheCategory, RedisCacheCategoryDao> {

    @Autowired
    RedisCacheCategoryDao redisCacheCategoryDao;
    
    public RedisCacheCategory getById(Integer id) {
        return redisCacheCategoryDao.findOne(id);
    }

    @Transactional(readOnly = false)
    public RedisCacheCategory save(RedisCacheCategory redisCacheCategory) {
        return redisCacheCategoryDao.save(redisCacheCategory);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisCacheCategoryDao.delete(id);
    }

    public Boolean isUniqueName(Integer id, String name) {
        RedisCacheCategory redisCacheCategory = redisCacheCategoryDao.isUniqueName(id, name);
        if (redisCacheCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueCode(Integer id, String code) {
        RedisCacheCategory redisCacheCategory = redisCacheCategoryDao.isUniqueCode(id, code);
        if (redisCacheCategory == null) {
            return true;
        } else {
            return false;
        }
    }

}