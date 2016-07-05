
package com.yihu.ehr.schema;

import org.springframework.stereotype.Component;

/**
 * Created by shine on 2016/6/30.
 */
@Component
public class IndicatorsDictKeySchema extends KeySchema {

    public String KeySchemaFormCode(String codeName){
        return makeKey("IndicatorsDict", codeName, "code");
    }
}