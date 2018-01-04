package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Service;

/**
 * Created by hzp on 2017.04.25
 */
@Service
public class RsMetadataKeySchema extends KeySchema {

    public RsMetadataKeySchema(){
        super.table="rs_metadata";
        super.column="dict_code";
    }

}
