package com.yihu.ehr.redis.pubsub.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.pubsub.dao.RedisMqPublisherDao;
import com.yihu.ehr.redis.pubsub.entity.RedisMqPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * redis消息发布者 Service
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
@Service
@Transactional
public class RedisMqPublisherService extends BaseJpaService<RedisMqPublisher, RedisMqPublisherDao> {

    @Autowired
    RedisMqPublisherDao redisMqPublisherDao;

    public RedisMqPublisher getById(Integer id) {
        return redisMqPublisherDao.findOne(id);
    }

    public List<RedisMqPublisher> findByChannel(String channel) {
        return redisMqPublisherDao.findByChannel(channel);
    }

    public RedisMqPublisher findByChannelAndAppId(String channel, String appId) {
        return redisMqPublisherDao.findByChannelAndAppId(channel, appId);
    }

    @Transactional(readOnly = false)
    public RedisMqPublisher save(RedisMqPublisher redisMqChannel) {
        return redisMqPublisherDao.save(redisMqChannel);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisMqPublisherDao.delete(id);
    }

    public Boolean isUniqueAppId(Integer id, String channel, String appId) {
        RedisMqPublisher redisMqPublisher = redisMqPublisherDao.isUniqueAppId(id, channel, appId);
        if (redisMqPublisher == null) {
            return true;
        } else {
            return false;
        }
    }

}