package com.yihu.ehr.event.listener;

import com.yihu.ehr.entity.event.EventProcessor;
import com.yihu.ehr.event.chain.EventDealChain;
import com.yihu.ehr.event.service.EventProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 程序启动完成后初始化事件处理链
 * Created by progr1mmer on 2018/7/5.
 */
@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private EventDealChain dealChain;
    @Autowired
    private EventProcessorService eventProcessorService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            List<EventProcessor> packEventProcessorList = eventProcessorService.search(null, "+createDate");
            for (EventProcessor packEventProcessor : packEventProcessorList) {
                dealChain.addProcessor(packEventProcessor.getName(), packEventProcessor.getProcessType(), packEventProcessor.isActive());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
