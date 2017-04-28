package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.common.KeySchema;
import org.springframework.stereotype.Component;

/**
 * 标准化数据 Redis Key生成器. 格式:
 *
 *  表名：主键值：列名
 *
 * 如：
 *  std_cda_versions:000000000000:name
 *
 * Created by hzp on 2017.04.25
 */
@Component
public class StdDataSetKeySchema extends KeySchema {

    private String DataSetTable = "std_data_set_";

    private String DataSetCodeColumn = "code";

    private String DataSetNameColumn = "name";

    private String DataSetIsMultiRecordColumn = "multi_record";

    public String dataSetCode(String version, String id){
        return get(makeKey(DataSetTable + version, id, DataSetCodeColumn));
    }

    public String dataSetName(String version, String id){
        return get(makeKey(DataSetTable + version, id, DataSetNameColumn));
    }

    public String dataSetNameByCode(String version, String code){
        return get(makeKey(DataSetTable + version, code, DataSetNameColumn));
    }

    public String dataSetMultiRecord(String version, String code){
        return get(makeKey(DataSetTable + version, code, DataSetIsMultiRecordColumn));
    }
}
