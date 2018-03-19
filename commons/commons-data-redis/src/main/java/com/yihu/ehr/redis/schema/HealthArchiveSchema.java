package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by wxw on 2018/3/14.
 */
@Component
public class HealthArchiveSchema extends KeySchema {
    public HealthArchiveSchema() {
        super.table = "HealthArchive";
        super.column = "HaName";
    }

    public void set(String key, Serializable val, long seconds) {
        super.redisClient.set(makeKey(table,key,column), val, seconds);
    }
}
