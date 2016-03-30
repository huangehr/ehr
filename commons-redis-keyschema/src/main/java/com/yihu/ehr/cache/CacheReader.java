package com.yihu.ehr.cache;

import com.yihu.ehr.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 18:29
 */
@Service
public class CacheReader {
    @Autowired
    private RedisClient redisClient;

    public <T> T read(String key){
        return redisClient.get(key);
    }
}
