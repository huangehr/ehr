package com.yihu.ehr.redis.cache.dao;

import com.yihu.ehr.redis.cache.entity.RedisCacheResponseTimeLog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 缓存获取的响应时间日志 DAO
 *
 * @author 张进军
 * @date 2017/12/4 08:56
 */
public interface RedisCacheResponseTimeLogDao extends PagingAndSortingRepository<RedisCacheResponseTimeLog, String> {

    List<RedisCacheResponseTimeLog> findByCacheKeyOrderByCreateDateAsc(@Param("cacheKey") String cacheKey);

}
