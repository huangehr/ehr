package com.yihu.ehr.resources;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RedisNamespace;
import com.yihu.ehr.redis.XRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 资源包装类.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 16:16
 */
@Service
public class TextResource {
    @Autowired
    Environment environment;

    @Autowired
    XRedisClient redisClient;

    static String makeKey(String subKey) {
        return RedisNamespace.TextResource + subKey;
    }

    
    public int getResourceCount() {
        return redisClient.keys(makeKey("*")).size();
    }
}
