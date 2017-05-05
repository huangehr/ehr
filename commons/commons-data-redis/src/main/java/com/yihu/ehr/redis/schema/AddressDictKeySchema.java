package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class AddressDictKeySchema extends KeySchema {

    public AddressDictKeySchema(){
        super.table="AddressDict";
        super.column="code";
    }

}
