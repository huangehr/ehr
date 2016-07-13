package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.web.RestTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.Set;

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
    ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    RestTemplates restTemplates;

    @Value("${svr-pack-mgr.server.port}")
    int port;

    @Scheduled(cron = "0/2 * * * * ?")
    public void sendResolveMessage() throws JsonProcessingException {
        Set<MPackage> packages = messageBuffer.pickMessages();
        for (MPackage pack : packages){
            redisTemplate.convertAndSend(Channel.PackageResolve, objectMapper.writeValueAsString(pack));
        }
    }


    @Scheduled(cron = "0/300 * * * * ?")
    public void sendResolveMessage1() throws JsonProcessingException {
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.set("count","500");
        map.set("filters","resourced=false");
        map.set("sort","+receiveDate");
        restTemplates.doPut("http://localhost:"+port+"/api/v1.0/message/resolve",map);
    }
}
