package com.yihu.ehr.task;

import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.01 14:35
 */
@Component
public class MessageSendTask {
    @Autowired
    MessageBuffer messageBuffer;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;

    @Scheduled(cron = "0/2 * * * * ?")
    public void sendResolveMessage(){
        Set<MPackage> packages = messageBuffer.pickMessages();
        for (MPackage pack : packages){
            redisTemplate.convertAndSend(Channel.PackageResolve, pack);
        }
    }
}
