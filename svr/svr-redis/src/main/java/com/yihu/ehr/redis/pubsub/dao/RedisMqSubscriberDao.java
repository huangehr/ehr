package com.yihu.ehr.redis.pubsub.dao;

import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * redis消息订阅者 DAO
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
public interface RedisMqSubscriberDao extends PagingAndSortingRepository<RedisMqSubscriber, Integer> {


}
