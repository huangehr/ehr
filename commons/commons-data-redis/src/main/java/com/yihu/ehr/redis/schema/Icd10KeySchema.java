package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.06.05
 */
@Component
public class Icd10KeySchema extends KeySchema {

    public Icd10KeySchema(){
        super.table = "icd10";
        super.column = "name";
    }

    /**
     * 获取对应健康问题redis
     * @param key
     * @return
     */
    public String getHpCode(String key) {
        return redisClient.get(makeKey(table, key,"hpCode"));
    }

    /**
     * 设置对应健康问题redis
     * @param key
     * @param value
     */
    public void setHpCode(String key, String value) {
        redisClient.set(makeKey(table, key,"hpCode"), value);
    }

    /**
     * 获取是否慢病信息，病包含类型 1-2 （如果没有类型则用0表示，例：1-0）
     * @param key
     * @return
     */
    public String getChronicInfo(String key) {
        return redisClient.get(makeKey(table, key,"chronic"));
    }

    /**
     * 设置是否为慢病
     * @param key
     * @param value
     */
    public void setChronicInfo(String key, String value) {
        redisClient.set(makeKey(table, key,"chronic"), value);
    }

    /**
     * 删除对应健康问题redis
     */
    public void deleteHpCode() {
        redisClient.delete(makeKey(table,"*","hpCode"));
    }

    /**
     * 删除慢病信息
     */
    public void deleteChronic() {
        redisClient.delete(makeKey(table,"*","chronic"));
    }

}
