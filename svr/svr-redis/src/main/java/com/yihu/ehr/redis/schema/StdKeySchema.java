package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.common.KeySchema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 标准化数据 Redis Key生成器. 格式为
 *
 *  表名：主键值：列名
 *
 * 如：
 *  std_cda_versions:000000000000:name
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.01 19:01
 */
@Component
public class StdKeySchema extends KeySchema {
    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.version-table-prefix}")
    private String VersionTable = "std_cda_versions";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.version-name}")
    private String VersionNameColumn = "name";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.data-set-table-prefix}")
    private String DataSetTable = "std_data_set_";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.data-set-code}")
    private String DataSetCodeColumn = "code";

    //@Value("${ehr.redis-key-com.yihu.ehr.redis.schema.std.data-set-name}")
    private String DataSetNameColumn = "name";

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

    public String versionName(String version){
        return makeKey(VersionTable, version, VersionNameColumn);
    }

    public String dataSetCode(String version, String id){
        return makeKey(DataSetTable + version, id, DataSetCodeColumn);
    }

    public String dataSetName(String version, String id){
        return makeKey(DataSetTable + version, id, DataSetNameColumn);
    }

    public String dataSetNameByCode(String version, String code){
        return makeKey(DataSetTable + version, code, DataSetNameColumn);
    }

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
