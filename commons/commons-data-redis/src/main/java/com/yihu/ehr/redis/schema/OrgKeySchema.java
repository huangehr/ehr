package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
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
}
