package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
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
        return redisClient.get(makeKey(DataSetTable + version, id, DataSetCodeColumn));
    }

    public void setDataSetCode(String version, String id,String value){
        redisClient.set(makeKey(DataSetTable + version, id, DataSetCodeColumn),value);
    }

    public String dataSetName(String version, String id){
        return redisClient.get(makeKey(DataSetTable + version, id, DataSetNameColumn));
    }

    public void setDataSetName(String version, String id,String value){
        redisClient.set(makeKey(DataSetTable + version, id, DataSetNameColumn),value);
    }

    public String dataSetNameByCode(String version, String code){
        return redisClient.get(makeKey(DataSetTable + version, code, DataSetNameColumn));
    }

    public void setDataSetNameByCode(String version, String code,String value){
        redisClient.set(makeKey(DataSetTable + version, code, DataSetNameColumn),value);
    }

    public Boolean dataSetMultiRecord(String version, String code){
        return redisClient.get(makeKey(DataSetTable + version, code, DataSetIsMultiRecordColumn));
    }

    public void setDataSetMultiRecord(String version, String code,boolean value){
        redisClient.set(makeKey(DataSetTable + version, code, DataSetIsMultiRecordColumn),value);
    }
}
