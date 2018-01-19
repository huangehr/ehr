
package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class IndicatorsDictKeySchema extends KeySchema {

    public IndicatorsDictKeySchema(){
        super.table="IndicatorsDict";
        super.column="code";
    }

}