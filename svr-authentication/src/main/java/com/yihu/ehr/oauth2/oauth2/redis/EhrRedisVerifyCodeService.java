package com.yihu.ehr.oauth2.oauth2.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by progr1mmer on 2018/4/18.
 */
public class EhrRedisVerifyCodeService {

    private final String KEY_SUFFIX = ":code";
    private final RedisTemplate<String, Serializable> redisTemplate;

    public EhrRedisVerifyCodeService(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void store (String client_id, String username, String code, long expire) {
        String key = client_id + ":" + username + KEY_SUFFIX;
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

    public Integer getExpireTime (String client_id, String username) {
        return new Long(redisTemplate.getExpire(client_id + ":" + username + KEY_SUFFIX)).intValue();
    }

    public boolean verification (String client_id, String username, String code) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }
        Serializable serializable = redisTemplate.opsForValue().get(client_id + ":" + username + KEY_SUFFIX);
        if (null == serializable) {
            return false;
        }
        String _code = serializable.toString();
        if (code.equalsIgnoreCase(_code)) {
            return true;
        }
        return false;
    }

}
