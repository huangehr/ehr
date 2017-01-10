package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by LYR-WORK on 2016/6/21.
 */
@Service
public class ResourceAdaptionDictSchema extends KeySchema {
    @Value("${ehr.redis-key-schema.resource-adapter-dict.table}")
    private String Table = "rs_adapter_dictionary";

    @Value("${ehr.redis-key-schema.resource-adapter-dict.column}")
    private String Column = "code_name";

    public String metaData(String cdaVersion, String dictCode, String srcDictEntryCode){
        return makeKey(Table, String.format("%s.%s.%s", cdaVersion, dictCode, srcDictEntryCode), Column);
    }
}
