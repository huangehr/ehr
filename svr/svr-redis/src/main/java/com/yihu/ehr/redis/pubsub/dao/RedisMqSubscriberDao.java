package com.yihu.ehr.redis.pubsub.dao;

import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * redis消息订阅者 DAO
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
public interface RedisMqSubscriberDao extends PagingAndSortingRepository<RedisMqSubscriber, Integer> {

    @Query(" FROM RedisMqSubscriber rmc WHERE rmc.id <> :id AND rmc.subscribedUrl = :subscribedUrl ")
    RedisMqSubscriber isUniqueSubscribedUrl(@Param("id") Integer id, @Param("subscribedUrl") String subscribedUrl);

}
