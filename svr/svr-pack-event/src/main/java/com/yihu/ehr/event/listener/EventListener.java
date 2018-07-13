package com.yihu.ehr.event.listener;

import com.yihu.ehr.event.model.Event;
import com.yihu.ehr.event.chain.EventDealChain;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 监听器
 * Created by progr1mmer on 2018/7/4.
 */
@Component
public class EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    private EventDealChain dealChain;

    @KafkaListener(topics = {"svr-event"})
    public void listen (ConsumerRecord consumerRecord){
        executorService.execute(() -> {
            LOGGER.info("{} - {} : {}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
            Event event = new Event((String)consumerRecord.key(), (String)consumerRecord.value());
            dealChain.deal(event);
        });
    }

}
