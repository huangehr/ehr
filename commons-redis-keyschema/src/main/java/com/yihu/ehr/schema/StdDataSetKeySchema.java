package com.yihu.ehr.schema;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 标准化数据 Redis Key生成器. 格式:
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
public class StdDataSetKeySchema extends KeySchema {
    @Value("${ehr.redis-key-schema.std.data-set-table-prefix}")
    private String DataSetTable = "std_data_set_";

    @Value("${ehr.redis-key-schema.std.data-set-code}")
    private String DataSetCodeColumn = "code";

    @Value("${ehr.redis-key-schema.std.data-set-name}")
    private String DataSetNameColumn = "name";

    @Value("${ehr.redis-key-schema.std.data-set-is-multi-record}")
    private String DataSetIsMultiRecordColumn = "multi_record";

    public String dataSetCode(String version, String id){
        return makeKey(DataSetTable + version, id, DataSetCodeColumn);
    }

    public String dataSetName(String version, String id){
        return makeKey(DataSetTable + version, id, DataSetNameColumn);
    }

    public String dataSetNameByCode(String version, String code){
        return makeKey(DataSetTable + version, code, DataSetNameColumn);
    }

    public String dataSetMultiRecord(String version, String code){
        return makeKey(DataSetTable + version, code, DataSetIsMultiRecordColumn);
    }
}
