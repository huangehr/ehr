package com.yihu.ehr.schema;

import org.springframework.stereotype.Component;

/**
 * Created by shine on 2016/7/5.
 */
@Component
public class HealthProblemDictKeySchema extends KeySchema {
    public String HpCodeToIcd10KeySchema(String HpCode){
        return makeKey(" HpCodeToIcd10", HpCode, "HpCode");
    }
}
