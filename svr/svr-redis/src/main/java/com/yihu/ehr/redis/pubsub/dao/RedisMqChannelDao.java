package com.yihu.ehr.redis.pubsub.dao;

import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * redis消息队列 DAO
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
public interface RedisMqChannelDao extends PagingAndSortingRepository<RedisMqChannel, Integer> {

    RedisMqChannel findByChannel(@Param("channel") String channel);

    @Query(" FROM RedisMqChannel rmc WHERE rmc.id <> :id AND rmc.channel = :channel ")
    RedisMqChannel isUniqueChannel(@Param("id") Integer id, @Param("channel") String channel);

    @Query(" FROM RedisMqChannel rmc WHERE rmc.id <> :id AND rmc.channelName = :channelName ")
    RedisMqChannel isUniqueChannelName(@Param("id") Integer id, @Param("channelName") String channelName);

}
