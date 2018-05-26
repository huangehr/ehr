package com.yihu.ehr.pack.service;

import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Redis管理
 * @author hzp add at 20170425
 */
@Service
public class RedisService {


    @Autowired
    private OrgKeySchema orgKeySchema;

    /**
     *获取机构名称redis
     * @return
     */
    public String getOrgName(String key) {
       return orgKeySchema.get(key);
    }

    /**
     *获取机构区域redis
     * @return
     */
    public String getOrgArea(String key) {
        return orgKeySchema.getOrgArea(key);
    }

}
