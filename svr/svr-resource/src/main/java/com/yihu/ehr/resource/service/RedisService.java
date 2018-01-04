package com.yihu.ehr.resource.service;



import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RsMetadataKeySchema rsMetadataKeySchema;

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsMetaData(String key) {
        return rsMetadataKeySchema.get(key);
    }
}
