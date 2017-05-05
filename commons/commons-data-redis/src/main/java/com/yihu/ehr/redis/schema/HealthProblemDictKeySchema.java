package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class HealthProblemDictKeySchema extends KeySchema {

    public HealthProblemDictKeySchema(){
        super.table="HpCodeToIcd10";
        super.column="HpCode";
    }
}
