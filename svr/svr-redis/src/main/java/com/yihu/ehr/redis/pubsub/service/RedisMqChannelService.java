package com.yihu.ehr.redis.pubsub.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.pubsub.dao.RedisMqChannelDao;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * redis消息队列 Service
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@Service
@Transactional
public class RedisMqChannelService extends BaseJpaService<RedisMqChannel, RedisMqChannelDao> {

    @Autowired
    RedisMqChannelDao redisMqChannelDao;
    
    public RedisMqChannel getById(Integer id) {
        return redisMqChannelDao.findOne(id);
    }

    public RedisMqChannel findByChannel(String channel) {
        return redisMqChannelDao.findByChannel(channel);
    }

    @Transactional(readOnly = false)
    public RedisMqChannel save(RedisMqChannel redisMqChannel) {
        return redisMqChannelDao.save(redisMqChannel);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisMqChannelDao.delete(id);
    }

    public Boolean isUniqueChannel(Integer id, String channel) {
        RedisMqChannel redisMqChannel = redisMqChannelDao.isUniqueChannel(id, channel);
        if (redisMqChannel == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueChannelName(Integer id, String channelName) {
        RedisMqChannel redisMqChannel = redisMqChannelDao.isUniqueChannelName(id, channelName);
        if (redisMqChannel == null) {
            return true;
        } else {
            return false;
        }
    }

}