package com.yihu.ehr.redis;

import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Redis 数据访问接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.04 11:12
 */
@Service(Services.RedisClient)
public class RedisClient implements XRedisClient {
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Override
    public void set(final String key, final Serializable value) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key_ = key.getBytes();
                byte[] value_ = toByteArray(value);
                connection.set(key_, value_);
                return true;

            }
        });
    }

    @Override
    public <T> T get(final String key) {
        return (T)redisTemplate.execute(new RedisCallback<Serializable>() {
            @Override
            public Serializable doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = key.getBytes();
                byte[] bytes = connection.get(keyBytes);
                return (Serializable) toObject(bytes);
            }
        });
    }

    @Override
    public void delete(String key) {
        redisTemplate.execute(new RedisCallback<Serializable>() {
            @Override
            public Serializable doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.del(key.getBytes());
            }
        });
    }

    @Override
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);

        redisTemplate.execute(new RedisCallback<Serializable>() {
            @Override
            public Serializable doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : keys){
                    connection.del(key.getBytes());
                }

                return null;
            }
        });
    }

    @Override
    public <T> Set<String> keys(String pattern) {
        return redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<byte[]> keys = connection.keys(pattern.getBytes());

                Set<String> returnKeys = new HashSet<String>();
                for (byte[] key : keys) {
                    returnKeys.add(new String(key));
                }

                return returnKeys;
            }
        });
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    /**
     * 对象转字节数组。
     * @param obj
     * @return
     */
    static byte[] toByteArray (Object obj) {
        if(obj == null) return null;

        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();

            bytes = byteOutputStream.toByteArray();

            objectOutputStream.close();
            byteOutputStream.close();
        } catch (IOException ex) {
            LogService.getLogger(RedisClient.class).error(ex.getMessage());
        }

        return bytes;
    }

    /**
     * 数组转对象。
     * @param bytes
     * @return
     */
    static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            if(bytes == null) return null;

            ByteArrayInputStream byteInputStream = new ByteArrayInputStream (bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream (byteInputStream);

            obj = objectInputStream.readObject();

            objectInputStream.close();
            byteInputStream.close();
        } catch (IOException ex) {
            LogService.getLogger(RedisClient.class).error(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            LogService.getLogger(RedisClient.class).error(ex.getMessage());
        }

        return obj;
    }
}
