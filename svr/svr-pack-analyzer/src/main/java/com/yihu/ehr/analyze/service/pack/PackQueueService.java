package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.model.packs.EsSimplePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;

@Service
public class PackQueueService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    //获取质控包
    public EsSimplePackage pop() throws IOException {
        Serializable serializable = redisTemplate.opsForList().rightPop(RedisCollection.AnalyzeQueue);
        if (serializable != null) {
            return objectMapper.readValue(serializable.toString(), EsSimplePackage.class);
        }
        return null;
    }

    //推送解析消息
    public void push (EsSimplePackage esSimplePackage) throws JsonProcessingException {
        if (esSimplePackage != null) {
            redisTemplate.opsForList().leftPush(RedisCollection.ResolveQueue, objectMapper.writeValueAsString(esSimplePackage));
        }
    }

}
