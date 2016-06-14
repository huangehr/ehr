package com.yihu.ehr.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Redis 数据访问接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.04 11:12
 */
@Service
public class RedisClient {
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 添加数据。
     *
     * @param key
     * @param value
     */
    public void set(final String key, final Serializable value) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] key_ = key.getBytes();
            byte[] value_ = SerializationUtils.serialize(value);
            connection.set(key_, value_);

            return true;
        });
    }

    /**
     * 批量设置key-value值。
     *
     * @param data
     */
    public void multiSet(Map<Serializable, Serializable> data){
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
                for(Serializable key : data.keySet()){
                    Serializable value = data.get(key);
                    connection.rPushX(SerializationUtils.serialize(key), SerializationUtils.serialize(value));
                }

                return null;
            }
        });
    }

    /**
     * 获取数据。
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(final String key) {
        return (T)redisTemplate.execute((RedisCallback<Serializable>) connection -> {
            byte[] keyBytes = key.getBytes();
            byte[] bytes = connection.get(keyBytes);
            if(bytes == null) return null;

            return (Serializable) SerializationUtils.deserialize(bytes);
        });
    }

    /**
     * 批量获取key关联的值。
     *
     * @param keys
     * @return
     */
    public List<Serializable> multiGet(Collection<String> keys){
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 删除。支持Key模式匹配删除。
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.execute((RedisCallback<Serializable>) connection -> connection.del(key.getBytes()));
    }

    /**
     * 删除多条记录。如果Key集合过大，建议使用Key模式匹配删除。
     *
     * @param keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 匹配特定模式的Key列表。
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<byte[]> keys = connection.keys(pattern.getBytes());

            Set<String> returnKeys = new HashSet<>();
            for (byte[] key : keys) {
                returnKeys.add(new String(key));
            }

            return returnKeys;
        });
    }

    /**
     * 是否包含指定Key。
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.exists(SerializationUtils.serialize(key)));
    }
}
