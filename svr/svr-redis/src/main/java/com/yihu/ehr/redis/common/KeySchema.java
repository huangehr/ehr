package com.yihu.ehr.redis.common;

import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.util.string.StringBuilderEx;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hzp on 2017.04.25
 */
public class KeySchema {
    @Autowired
    private RedisClient redisClient;

    protected String table="table";
    protected String column="column";

    protected String keySchema = "%1:%2:%3";



    protected String makeKey(String table,String key,String column){
        return new StringBuilderEx(keySchema)
                .arg(table)
                .arg(key)
                .arg(column)
                .toString();
    }

    public  <T> T get(String key){
        return redisClient.get(makeKey(table,key,column));
    }

    public void set( String key, String val){
        redisClient.set(makeKey(table,key,column),val);
    }
}
