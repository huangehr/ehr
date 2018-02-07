package com.yihu.ehr.redis.pubsub.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.pubsub.dao.RedisMqSubscriberDao;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * redis消息订阅者 Service
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@Service
@Transactional
public class RedisMqSubscriberService extends BaseJpaService<RedisMqSubscriber, RedisMqSubscriberDao> {

    @Autowired
    RedisMqSubscriberDao redisMqSubscriberDao;

    public RedisMqSubscriber getById(Integer id) {
        return redisMqSubscriberDao.findOne(id);
    }

    public List<RedisMqSubscriber> findByChannel(String channel) {
        return redisMqSubscriberDao.findByChannel(channel);
    }

    @Transactional(readOnly = false)
    public RedisMqSubscriber save(RedisMqSubscriber redisMqChannel) {
        return redisMqSubscriberDao.save(redisMqChannel);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisMqSubscriberDao.delete(id);
    }

    public Boolean isUniqueAppId(Integer id, String channel, String appId) {
        RedisMqSubscriber redisMqSubscriber = redisMqSubscriberDao.isUniqueAppId(id, channel, appId);
        if (redisMqSubscriber == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueSubscribedUrl(Integer id, String channel, String subscriberUrl) {
        RedisMqSubscriber redisMqSubscriber = redisMqSubscriberDao.isUniqueSubscribedUrl(id, channel, subscriberUrl);
        if (redisMqSubscriber == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isExist(String channel, String subscriber) {
        RedisMqSubscriber redisMqSubscriber = redisMqSubscriberDao.findByChannelAndAndSubscribedUrl(channel, subscriber);
        return redisMqSubscriber != null;
    }

    public void unsubscribe(String channel, String subscriber) {
        List<RedisMqSubscriber> subscriberList = redisMqSubscriberDao.findByChannel(channel);
        subscriberList.forEach(redisMqSubscriber -> {
            if (StringUtils.isEmpty(subscriber)) {//取消所有订阅者
                redisMqSubscriberDao.delete(redisMqSubscriber);
            } else if (subscriber.equals(redisMqSubscriber.getSubscribedUrl())) {
                redisMqSubscriberDao.delete(redisMqSubscriber);//取消指定订阅者
            }
        });
    }

}