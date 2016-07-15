package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.model.packs.MPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(cron = "0/2 * * * * ?")
    public void sendResolveMessage() throws JsonProcessingException {
        Set<MPackage> packages = messageBuffer.pickMessages();
        for (MPackage pack : packages){
            redisTemplate.convertAndSend(Channel.PackageResolve, objectMapper.writeValueAsString(pack));
        }
    }
}
