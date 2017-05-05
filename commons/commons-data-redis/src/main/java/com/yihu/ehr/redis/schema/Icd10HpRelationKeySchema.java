package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class Icd10HpRelationKeySchema extends KeySchema {

    public Icd10HpRelationKeySchema(){
        super.table="hp_icd10_relation";
        super.column="icd10_code";
    }
}
