package com.yihu.ehr.redis.pubsub.dao;

import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * redis消息记录 DAO
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
public interface RedisMqMessageLogDao extends PagingAndSortingRepository<RedisMqMessageLog, String> {

    List<RedisMqMessageLog> findByChannel(@Param("channel") String channel);

    @Modifying
    @Query("update RedisMqMessageLog set status=:status where id=:id")
    void updateStatus(@Param("id") String id, @Param("status") Integer status);

}
