package com.yihu.ehr.kafka;

import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by progr1mmer on 2018/8/2.
 */
public class ConsumerRunner implements Runnable {

    private KafkaConsumer<String, String> consumer;
    private List<RedisMqSubscriber> redisMqSubscribers;
    private RestTemplate restTemplate;

    public ConsumerRunner(String groupId, Set<String> topics) throws Exception {
        Assert.notEmpty(topics, "Topic cannot be empty");
        init(groupId, topics);
    }

    private void init(String groupId, Set<String> topics) throws Exception {
        Properties props = new Properties();
        Environment environment = SpringContext.getService(Environment.class);
        props.put("bootstrap.servers", environment.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        List<String> _topics = new ArrayList<>();
        _topics.addAll(topics);
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(_topics);
        RedisMqSubscriberService redisMqSubscriberService = SpringContext.getService(RedisMqSubscriberService.class);
        redisMqSubscribers = redisMqSubscriberService.search("appId=" + groupId + ";channel=" + StringUtils.join(_topics, ","));
        restTemplate = SpringContext.getService("restTemplate");
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                for (RedisMqSubscriber subscriber : redisMqSubscribers) {
                    if (record.topic().equals(subscriber.getChannel())) {
                        String subscribedUrl = subscriber.getSubscribedUrl();
                        try {
                            // 推送消息到指定服务地址
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                            HttpEntity<String> entity = new HttpEntity<>(record.offset() + ":" + record.key() + ":" + record.value(), headers);
                            restTemplate.postForObject(subscribedUrl, entity, String.class);
                            consumer.commitSync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
}
