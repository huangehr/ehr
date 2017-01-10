package com.yihu.ehr.schema;

import org.springframework.stereotype.Component;

/**
 * Created by shine on 2016/7/5.
 */
@Component
public class AddressDictKeySchema extends KeySchema{
    public String AddressDictKeySchema(String codeName){
        return makeKey(" AddressDict", codeName, "code");
    }
}
