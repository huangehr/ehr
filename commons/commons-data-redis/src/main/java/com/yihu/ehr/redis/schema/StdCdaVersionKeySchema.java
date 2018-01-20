package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class StdCdaVersionKeySchema extends KeySchema {
    public StdCdaVersionKeySchema(){
        super.table="std_cda_versions";
        super.column="name";
    }

}
