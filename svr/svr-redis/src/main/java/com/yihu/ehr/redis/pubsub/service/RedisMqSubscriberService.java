package com.yihu.ehr.redis.pubsub.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.pubsub.dao.RedisMqSubscriberDao;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * redis消息订阅者 Service
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@Service
@Transactional
public class RedisMqSubscriberService extends BaseJpaService<RedisMqSubscriber, RedisMqSubscriberDao> {


}