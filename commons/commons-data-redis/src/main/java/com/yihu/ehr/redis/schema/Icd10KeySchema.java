package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hzp on 2017.06.05
 */
@Component
public class Icd10KeySchema extends KeySchema {

    public Icd10KeySchema(){
        super.table="icd10";
        super.column="name";
    }

    /**
     * 获取对应健康问题redis
     */
    public String getHpCode(String key)
    {
        return redisClient.get(makeKey(table,key,"hpCode"));
    }

    /**
     * 设置对应健康问题redis
     * @return
     */
    public void setHpCode(String key,String value)
    {
        redisClient.set(makeKey(table,key,"hpCode"),value);
    }

    /**
     * 删除对应健康问题redis
     */
    public void deleteHpCode()
    {
        redisClient.delete(makeKey(table,"*","hpCode"));
    }

}
