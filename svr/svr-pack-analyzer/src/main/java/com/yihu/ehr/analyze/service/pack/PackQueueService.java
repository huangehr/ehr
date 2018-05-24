package com.yihu.ehr.analyze.service.pack;

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

    public EsSimplePackage pop() throws IOException {
        Serializable serializable = redisTemplate.opsForList().rightPop(RedisCollection.AnalyzeQueue);
        if (serializable != null) {
            String packStr = serializable.toString();
            return objectMapper.readValue(packStr, EsSimplePackage.class);
        }
        return null;
    }
}
