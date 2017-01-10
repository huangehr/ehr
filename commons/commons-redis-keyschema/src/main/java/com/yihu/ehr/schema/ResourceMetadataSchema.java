package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by lyr on 2016/6/21.
 */
@Service
public class ResourceMetadataSchema extends KeySchema{
    @Value("${ehr.redis-key-schema.resource-metadata.table}")
    private String Table = "rs_metadata";

    @Value("${ehr.redis-key-schema.resource-metadata.column}")
    private String Column = "dict_code";

    public String metaData(String metadataId){
        return makeKey(Table, metadataId, Column);
    }
}
