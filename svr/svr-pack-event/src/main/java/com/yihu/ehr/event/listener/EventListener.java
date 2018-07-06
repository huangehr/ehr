package com.yihu.ehr.event.listener;

import com.yihu.ehr.event.model.Event;
import com.yihu.ehr.event.chain.EventDealChain;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created by progr1mmer on 2018/7/4.
 */
@Component
public class EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    @Autowired
    private EventDealChain dealChain;

    @KafkaListener(topics = {"svr-event"})
    public void listen (ConsumerRecord consumerRecord){
        LOGGER.info("{} - {} : {}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
        Event event = new Event((String)consumerRecord.key(), (String)consumerRecord.value());
        dealChain.deal(event);
    }
}
