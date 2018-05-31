package com.yihu.ehr.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 定时定量处理消息，包括：
 * - 发送消息。
 * - 重试订阅失败的消息。
 * - 处理订阅成功的消息。
 *
 * @author 张进军
 * @date 2018/5/7 14:01
 */
@Component
public class PubSubMessageJob {

    private Logger logger = LoggerFactory.getLogger(PubSubMessageJob.class);

    // 最大次数尝试重发订阅失败消息
    private final int maxFailedNum = 3;
    // 每次定时最多操作的消息数量
    private final int onceNum = 1000;

    @Autowired
    RedisMqChannelService redisMqChannelService;
    @Autowired
    RedisMqMessageLogService redisMqMessageLogService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    /*
     * 发送消息
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void send() {
        try {
            int num = 0;
            while (true) {
                Object msgObj = redisTemplate.opsForList().rightPop(RedisCollection.PUB_WAITING_MESSAGES);
                if (msgObj == null) {
                    break;
                }
                Map<String, Object> messageMap = objectMapper.readValue(String.valueOf(msgObj), Map.class);
                String channel = messageMap.get("channel").toString();

                // 发布消息
                redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(messageMap));
                // 累计入列数
                RedisMqChannel afterChannel = updateChannelQueueNumber(channel, "Enqueued");

                num++;
                if (num == onceNum) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 重试订阅失败的消息
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void resend() {
        try {
            int num = 0;
            while (true) {
                Object msgObj = redisTemplate.opsForList().rightPop(RedisCollection.SUB_FAILED_MESSAGES);
                if (msgObj == null) {
                    break;
                }

                boolean valid = true;
                RedisMqMessageLog cacheMessageLog = objectMapper.readValue(String.valueOf(msgObj), RedisMqMessageLog.class);
                if (cacheMessageLog.getFailedNum() == 0) {
                    // 头次订阅失败，则保存到数据库中
                    cacheMessageLog.setFailedNum(1);
                    redisMqMessageLogService.save(cacheMessageLog);
                } else if (cacheMessageLog.getFailedNum() == -1) {
                    // 累计订阅失败次数
                    RedisMqMessageLog dbMessageLog = redisMqMessageLogService.getById(cacheMessageLog.getId());
                    if (dbMessageLog.getFailedNum() >= maxFailedNum) {
                        // 大于等于最大尝试失败数，则不再重试。
                        valid = false;
                    } else {
                        dbMessageLog.setFailedNum(dbMessageLog.getFailedNum() + 1);
                        dbMessageLog.setErrorMsg(cacheMessageLog.getErrorMsg());
                        redisMqMessageLogService.save(dbMessageLog);
                    }
                }

                if (valid) {
                    // 将消息加入到待发缓存集合中
                    redisMqChannelService.addToPubWaitingMessage(cacheMessageLog.getPublisherAppId(),
                            cacheMessageLog.getChannel(), cacheMessageLog.getMessage(), cacheMessageLog.getId());

                    num++;
                    if (num == onceNum) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 处理订阅成功的消息。
     * 累计出列数、更新重试成功的订阅失败消息的状态。
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void update() {
        try {
            int num = 0;
            while (true) {
                Object msgObj = redisTemplate.opsForList().rightPop(RedisCollection.SUB_SUCCESSFUL_MESSAGES);
                if (msgObj == null) {
                    break;
                }

                RedisMqMessageLog cacheMessageLog = objectMapper.readValue(String.valueOf(msgObj), RedisMqMessageLog.class);
                RedisMqMessageLog dbMessageLog = redisMqMessageLogService.getById(cacheMessageLog.getId());
                if (dbMessageLog != null && dbMessageLog.getStatus() == 0) {
                    // 更新重试成功的订阅失败消息的状态
                    redisMqMessageLogService.updateStatus(cacheMessageLog.getId(), 1);
                }

                // 累计出列数
                RedisMqChannel afterChannel = updateChannelQueueNumber(cacheMessageLog.getChannel(), "Dequeued");

                num++;
                if (num == onceNum) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 累计 channel 的出入列数
     *
     * @param channel 队列编码
     * @param type    出入列类型标识符
     * @return RedisMqChannel
     */
    private synchronized RedisMqChannel updateChannelQueueNumber(String channel, String type) {
        RedisMqChannel mqChannelAfter = null;
        RedisMqChannel mqChannel = redisMqChannelService.findByChannel(channel);
        if ("Dequeued".equals(type)) {
            // 累计出列数
            mqChannel.setDequeuedNum(mqChannel.getDequeuedNum() + 1);
            mqChannelAfter = redisMqChannelService.save(mqChannel);
        } else if ("Enqueued".equals(type)) {
            // 累计入列数
            mqChannel.setEnqueuedNum(mqChannel.getEnqueuedNum() + 1);
            mqChannelAfter = redisMqChannelService.save(mqChannel);
        }
        return mqChannelAfter;
    }

}
