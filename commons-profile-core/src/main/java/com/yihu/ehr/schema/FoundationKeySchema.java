package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.19 12:02
 */
@Component
public class FoundationKeySchema extends KeySchema {
    @Value("${redis-key-schema.org.table}")
    private String OrgTable;

    @Value("${redis-key-schema.org.name-column}")
    private String NameColumn;

    public String orgKey(String code){
        return makeKey(OrgTable, code, NameColumn);
    }
}
