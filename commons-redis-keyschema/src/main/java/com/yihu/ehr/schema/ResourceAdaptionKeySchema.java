package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author Sand
 * @created 2016.05.17 16:03
 */
public class ResourceAdaptionKeySchema extends KeySchema {
    @Value("${ehr.redis-key-schema.resource-adaption.table}")
    private String Table = "table";

    @Value("${ehr.redis-key-schema.resource-adaption.code}")
    private String ResourceMetaData = "resource_metadata";

    public String metaData(String cdaVersion, String dataSet, String ehrMetaData){
        return makeKey(Table, String.format("%s.%s.%s", cdaVersion, dataSet, ehrMetaData), ResourceMetaData);
    }
}
