package com.yihu.ehr.kafka;

import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by progr1mmer on 2018/8/2.
 */
//@Component
public class MessageManager {

    private ExecutorService executorService;

    @Autowired
    private RedisMqChannelService redisMqChannelService;
    @Autowired
    private RedisMqSubscriberService redisMqSubscriberService;

    public void initConsumer () {
        try {
            Map<String, Set<String>> groups = new HashMap<>();
            List<RedisMqChannel> channels = redisMqChannelService.search("");
            for (RedisMqChannel channel : channels) {
                List<RedisMqSubscriber> redisMqSubscribers = redisMqSubscriberService.search("channel=" + channel.channel);
                redisMqSubscribers.forEach(item -> {
                    if (groups.containsKey(item.getAppId())) {
                        groups.get(item.getAppId()).add(item.getChannel());
                    } else {
                        Set<String> topics = new HashSet<>();
                        topics.add(item.getChannel());
                        groups.put(item.getAppId(), topics);
                    }
                });
            }
            executorService = Executors.newFixedThreadPool(groups.size());
            for (String key : groups.keySet()) {
                executorService.execute(new ConsumerRunner(key, groups.get(key)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
