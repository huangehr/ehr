package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class OrgKeySchema extends KeySchema {

    public OrgKeySchema(){
        super.table="organizations";
        super.column="name";
    }


    /**
     * 获取机构区域Redis
     */
    public String getOrgArea(String key)
    {
          return get("Code_Area:"+key);
    }

    /**
     *设置机构区域Redis
     */
    public void setOrgArea(String key,String value)
    {
        set("Code_Area:"+key,value);
    }

    /**
     * 删除机构区域Redis
     */
    public void deleteOrgArea()
    {
        delete("Code_Area:*");
    }

    /**
     * 获取机构SAAS区域权限范围redis
     * @return
     */
    public String getOrgSaasArea(String key)
    {
        return redisClient.get(makeKey(table,key,"saasArea"));
    }

    /**
     * 设置机构SAAS区域权限范围redis
     * @return
     */
    public void setOrgSaasArea(String key,String value)
    {
        redisClient.set(makeKey(table,key,"saasArea"),value);
    }

    /**
     * 删除机构SAAS区域权限范围redis
     */
    public void deleteOrgSaasArea()
    {
        redisClient.delete(makeKey(table,"*","saasArea"));
    }


    /**
     * 获取机构SAAS机构权限范围redis
     * @return
     */
    public String getOrgSaasOrg(String key)
    {
        return redisClient.get(makeKey(table,key,"saasOrg"));
    }

    /**
     * 设置机构SAAS机构权限范围redis
     * @return
     */
    public void setOrgSaasOrg(String key,String value)
    {
        redisClient.set(makeKey(table,key,"saasOrg"),value);
    }

    /**
     * 删除机构SAAS机构权限范围redis
     */
    public void deleteOrgSaasOrg()
    {
        redisClient.delete(makeKey(table,"*","saasOrg"));
    }
}
