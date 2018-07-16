package com.yihu.ehr.event.listener;

import com.yihu.ehr.event.model.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 监听器
 * Created by progr1mmer on 2018/7/4.
 */
@Component
public class EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);
    private static final BlockingQueue<Event> EVENT_BLOCKING_QUEUE = new ArrayBlockingQueue<>(50000);

    @KafkaListener(topics = {"svr-event"})
    public void listen (ConsumerRecord consumerRecord){
        while (EVENT_BLOCKING_QUEUE.size() >= 49990) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("{} - {} : {}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
        EVENT_BLOCKING_QUEUE.add(new Event((String)consumerRecord.key(), (String)consumerRecord.value()));
    }

    public static Event poll() {
        return EVENT_BLOCKING_QUEUE.poll();
    }

}
