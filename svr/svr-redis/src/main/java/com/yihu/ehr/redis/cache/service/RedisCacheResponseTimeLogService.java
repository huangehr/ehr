package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.cache.dao.RedisCacheResponseTimeLogDao;
import com.yihu.ehr.redis.cache.entity.RedisCacheResponseTimeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 缓存获取的响应时间日志 Service
 *
 * @author 张进军
 * @date 2017/12/4 08:56
 */
@Service
@Transactional
public class RedisCacheResponseTimeLogService extends BaseJpaService<RedisCacheResponseTimeLog, RedisCacheResponseTimeLogDao> {

    @Autowired
    RedisCacheResponseTimeLogDao redisCacheResponseTimeLogDao;

    public List<RedisCacheResponseTimeLog> findByCacheKey(String key) {
        return redisCacheResponseTimeLogDao.findByCacheKeyOrderByCreateDateAsc(key);
    }

    @Transactional(readOnly = false)
    public RedisCacheResponseTimeLog save(RedisCacheResponseTimeLog redisCacheResponseTimeLog) {
        return redisCacheResponseTimeLogDao.save(redisCacheResponseTimeLog);
    }

}