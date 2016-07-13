package com.yihu.ehr.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yihu.ehr.queue.MessageBuffer;
import com.yihu.ehr.web.RestTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * 消息发送任务。作为一个子功能存在于档案包管理器中。一旦接收到一个新的档案包，
 * 档案暂时存放于缓存中，此任务向解析服务发送消息。此方法用于解耦控制器与消息队列。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.01 14:35
 */
@Component
public class MessageSendTask {
    @Autowired
    MessageBuffer messageBuffer;

    @Autowired
    RestTemplates restTemplates;

    @Value("${svr-pack-resolve.server.port}")
    int port;

    @Scheduled(cron = "0/5 * * * * ?")
    public void sendResolveMessage1() throws JsonProcessingException {
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.set("count","500");
        map.set("filters","a1");
        restTemplates.doPost("http://localhost:"+port+"/api/v1.0/message/resolve",map);
    }
}
