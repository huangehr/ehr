package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.string.StringBuilderEx;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by hzp on 2017.04.25
 */
public class KeySchema {

    protected final String keySchema = "%1:%2:%3";
    protected String table = "table";
    protected String column = "column";

    @Autowired
    protected RedisClient redisClient;

    /**
     * 获取组合键值 table:key:column
     * @param table 表名
     * @param key 主键
     * @param column 列名
     * @return
     */
    public String makeKey(String table, String key, String column) {
        return new StringBuilderEx(keySchema)
                .arg(table)
                .arg(key)
                .arg(column)
                .toString();
    }

    /**
     * 获取单条缓存
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        return redisClient.get(makeKey(table, key, column));
    }

    /**
     * 保存单条缓存
     * @param key
     * @param val
     */
    public void set(String key, Serializable val){
        redisClient.set(makeKey(table, key, column), val);
    }

    /**
     * 删除单条缓存
     * @param key
     */
    public void delete(String key) {
        redisClient.delete(makeKey(table, key, column));
    }

    /**
     * 删除默认缓存
     */
    public void deleteAll(){
        redisClient.delete(makeKey(table,"*", column));
    }

    /**
     * 获取所有缓存数据
     */
    public Map<String,Object> getAll(){
        Map<String, Object> re = new HashMap<>();
        Set<String> keys = redisClient.keys(makeKey(table,"*", column));
        for (String key : keys) {
            String val = redisClient.get(key);
            re.put(key,val);
        }
        return re;
    }

    /**
     * 判断是否存在
     */
    public boolean hasKey(String key){
        return redisClient.hasKey(makeKey(table, key, column));
    }
}
