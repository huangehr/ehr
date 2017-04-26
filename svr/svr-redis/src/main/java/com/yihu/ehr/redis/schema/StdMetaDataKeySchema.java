package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.common.KeySchema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @created 2016.05.19 13:56
 */
@Component
public class StdMetaDataKeySchema extends KeySchema {
    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.meta-data-table-prefix}")
    private String MetaDataTable = "std_meta_data_";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.meta-data-dict-id}")
    private String MetaDataDictIdColumn = "dict_id";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.meta-data-type}")
    private String MetaDataTypeColumn = "type";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.dict-entry-table-prefix}")
    private String DictEntryTable = "std_dictionary_entry_";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.dict-entry-value}")
    private String DictEntryValueColumn = "value";

    public String metaDataDict(String version, String dataSetCode, String innerCode) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataDictIdColumn);
    }

    public String metaDataType(String version, String dataSetCode, String innerCode) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataTypeColumn);
    }

    public String dictEntryValue(String version, String dictId, String entryCode) {
        assert StringUtils.isNotBlank(version) && StringUtils.isNotBlank(entryCode);

        return makeKey(DictEntryTable + version, dictId + "." + entryCode, DictEntryValueColumn);
    }
}
