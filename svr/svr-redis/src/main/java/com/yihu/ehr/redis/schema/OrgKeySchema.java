package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.common.KeySchema;
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

}
