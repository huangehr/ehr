package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.common.KeySchema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 *  Created by hzp on 2017.04.25
 */
@Component
public class StdMetaDataKeySchema extends KeySchema {
    private String MetaDataTable = "std_meta_data_";

    private String MetaDataDictIdColumn = "dict_id";

    private String MetaDataTypeColumn = "type";

    private String DictEntryTable = "std_dictionary_entry_";

    private String DictEntryValueColumn = "value";

    public String metaDataDict(String version, String dataSetCode, String innerCode) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return get(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataDictIdColumn));
    }

    public String metaDataType(String version, String dataSetCode, String innerCode) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return get(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataTypeColumn));
    }

    public String dictEntryValue(String version, String dictId, String entryCode) {
        assert StringUtils.isNotBlank(version) && StringUtils.isNotBlank(entryCode);

        return get(makeKey(DictEntryTable + version, dictId + "." + entryCode, DictEntryValueColumn));
    }
}
