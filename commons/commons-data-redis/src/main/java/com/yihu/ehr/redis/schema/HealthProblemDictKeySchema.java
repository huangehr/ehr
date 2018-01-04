package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class HealthProblemDictKeySchema extends KeySchema {

    public HealthProblemDictKeySchema(){
        super.table="HealthProblemDict";
        super.column="HpName";
    }
}
