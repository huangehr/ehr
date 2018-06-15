package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 * Created by progr1mmer on 2018/6/15.
 */
@Component
public class RsMetadataKeySchema extends KeySchema {

    public RsMetadataKeySchema(){
        super.table="rs_metadata";
        super.column="dict_code";
    }

}
