
package com.yihu.ehr.schema;

/**
 * Created by shine on 2016/6/30.
 */
public class IndicatorsDictKeySchema extends KeySchema {

    public String KeySchemaFormCode(String codeName){
        return makeKey("IndicatorsDict", codeName, "code");
    }
}