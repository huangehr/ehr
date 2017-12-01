package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.cache.dao.RedisCacheAuthorizationDao;
import com.yihu.ehr.redis.cache.entity.RedisCacheAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缓存授权 Service
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@Service
@Transactional
public class RedisCacheAuthorizationService extends BaseJpaService<RedisCacheAuthorization, RedisCacheAuthorizationDao> {

    @Autowired
    RedisCacheAuthorizationDao redisCacheAuthorizationDao;
    
    public RedisCacheAuthorization getById(Integer id) {
        return redisCacheAuthorizationDao.findOne(id);
    }

    public RedisCacheAuthorization findByCategoryCodeAndAppId(String categoryCode, String appId) {
        return redisCacheAuthorizationDao.findByCategoryCodeAndAppId(categoryCode, appId);
    }

    @Transactional(readOnly = false)
    public RedisCacheAuthorization save(RedisCacheAuthorization redisCacheAuthorization) {
        return redisCacheAuthorizationDao.save(redisCacheAuthorization);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisCacheAuthorizationDao.delete(id);
    }

    public Boolean isUniqueAppId(Integer id, String categoryCode, String appId) {
        RedisCacheAuthorization redisCacheAuthorization = redisCacheAuthorizationDao.isUniqueAppId(id, categoryCode, appId);
        if (redisCacheAuthorization == null) {
            return true;
        } else {
            return false;
        }
    }

}