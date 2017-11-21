package com.yihu.ehr.redis.pubsub.dao;

import com.yihu.ehr.redis.pubsub.entity.RedisMqPublisher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * redis消息发布者 DAO
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
public interface RedisMqPublisherDao extends PagingAndSortingRepository<RedisMqPublisher, Integer> {

    List<RedisMqPublisher> findByChannel(@Param("channel") String channel);

    RedisMqPublisher findByChannelAndAppId(@Param("channel") String channel, @Param("appId") String appId);

    @Query(" FROM RedisMqPublisher rmp WHERE rmp.id <> :id AND rmp.channel = :channel AND rmp.appId = :appId ")
    RedisMqPublisher isUniqueAppId(@Param("id") Integer id, @Param("channel") String channel, @Param("appId") String appId);

}
