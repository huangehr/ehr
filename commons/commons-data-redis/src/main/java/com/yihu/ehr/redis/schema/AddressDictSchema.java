package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 *  行政区划
 * @author HZY
 * @created 2018/8/21 16:58
 */
@Component
public class AddressDictSchema extends KeySchema {


    public AddressDictSchema() {
        super.table = "area";
        super.column = "name";
    }


    /**
     * 清空当前表的所有缓存
     */
    public void delete(){
        redisClient.delete(makeKey(this.table , "*",column));
    }


    public String getAreaName( String code) {
        return redisClient.get(makeKey(table, code, column));
    }

    public void setAreaName( String code, String value) {
        redisClient.set(makeKey(table, code, column), value);
    }


}
