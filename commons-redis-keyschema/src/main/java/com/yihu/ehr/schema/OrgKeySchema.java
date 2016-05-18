package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 18:01
 */
@Component
public class OrgKeySchema extends KeySchema {
    @Value("${ehr.redis-key-schema.org.table}")
    private String Table = "table";

    @Value("${ehr.redis-key-schema.org.name}")
    private String Name = "name";

    public String name(String code){
        return makeKey(Table, code, Name);
    }
}
