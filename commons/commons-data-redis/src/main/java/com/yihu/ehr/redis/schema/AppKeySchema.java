package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Component;

/**
 * Created by janseny 2017年11月20日
 */
@Component
public class AppKeySchema extends KeySchema {

    public AppKeySchema(){
        super.table="FrontAppSession";
        super.column="code";
    }

    /**
     * 前端app 保存单条缓存
     * @return
     */
    public void setRedisApp(String key, String val){
        if(key.contains("-")){
            String keyVal = key.substring(0,key.indexOf("-"));
            String code =  key.substring(key.indexOf("-")+1, key.length());
            redisClient.set(makeKey(table,keyVal,code),val);
        }else {
            set(key,val);
        }
    }

    /**
     * 前端app 获取单条缓存
     * @return
     */
    public String getRedisApp(String key){
        if(key.contains("-")){
            String keyVal = key.substring(0,key.indexOf("-"));
            String code =  key.substring(key.indexOf("-")+1, key.length());
           return redisClient.get(makeKey(table, keyVal, code));
        }else {
          return   get(key);
        }
    }

    /**
     * 前端app 删除单条缓存
     * @return
     */
    public void deleteRedisApp(String key){
        if(key.contains("-")){
            String keyVal = key.substring(0,key.indexOf("-"));
            String code =  key.substring(key.indexOf("-")+1, key.length());
             redisClient.delete(makeKey(table, keyVal, code));
        }else {
             delete(key);
        }
    }



}
