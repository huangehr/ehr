package com.yihu.ehr.redis.pubsub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.pubsub.dao.RedisMqChannelDao;
import com.yihu.ehr.redis.pubsub.dao.RedisMqPublisherDao;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqPublisher;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * redis消息队列 Service
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@Service
public class RedisMqChannelService extends BaseJpaService<RedisMqChannel, RedisMqChannelDao> {

    private static final Logger logger = Logger.getLogger(RedisMqChannelService.class);

    @Autowired
    RedisMqChannelDao redisMqChannelDao;
    @Autowired
    RedisMqPublisherDao redisMqPublisherDao;
    @Autowired
    RedisMqMessageLogService redisMqMessageLogService;
    @Autowired
    RedisMqChannelService redisMqChannelService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

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

    public Boolean isExist(String channel) {
        RedisMqChannel channels = redisMqChannelDao.findByChannel(channel);
        return channels != null;
    }

    /**
     * 发布消息
     *
     * @param publisherAppId 发布者应用ID
     * @param channel        消息队列编码
     * @param message        消息
     * @return Envelop
     */
    public Envelop sendMessage(String publisherAppId, String channel, String message) {
        Envelop envelop = new Envelop();

        try {
            // 判断消息队列是否注册
            RedisMqChannel redisMqChannel = redisMqChannelDao.findByChannel(channel);
            if (redisMqChannel == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("消息队列 " + channel + " 还未注册，需要先注册才能往队列发布消息。");
                return envelop;
            }
            // 判断队列是否绑定发布者
            RedisMqPublisher redisMqPublisher = redisMqPublisherDao.findByChannelAndAppId(channel, publisherAppId);
            if (redisMqPublisher == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("消息队列 " + channel + " 中没绑定过应用ID为 " + publisherAppId + " 的发布者，需要先绑定才能发布消息。");
                return envelop;
            }

            // 将消息加入到待发缓存集合中
            addToPubWaitingMessage(publisherAppId, channel, message, "");

            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("发布消息发生异常：" + e.getMessage());
        }
        return envelop;
    }

    /**
     * 将消息加入到待发缓存集合中
     *
     * @param publisherAppId 发布者应用ID
     * @param channel        消息队列编码
     * @param message        消息
     * @param messageId      消息ID，订阅失败重发时才有值，不然为空字符串，即首次发送必为空字符串。
     * @throws JsonProcessingException
     */
    public void addToPubWaitingMessage(String publisherAppId, String channel, String message, String messageId)
            throws JsonProcessingException {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageId", messageId);
        messageMap.put("channel", channel);
        messageMap.put("publisherAppId", publisherAppId);
        messageMap.put("messageContent", message);
        redisTemplate.opsForList().leftPush(RedisCollection.PUB_WAITING_MESSAGES, objectMapper.writeValueAsString(messageMap));
    }

}