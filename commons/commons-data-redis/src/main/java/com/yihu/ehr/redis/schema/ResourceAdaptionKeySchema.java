package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Service;

/**
 * Created by hzp on 2017.04.25
 */
@Service
public class ResourceAdaptionKeySchema extends KeySchema {

    public ResourceAdaptionKeySchema(){
        super.table="rs_adapter_metadata";
        super.column="resource_metadata";
    }

    public String getMetaData(String cdaVersion, String dataSet, String ehrMetaData){
        return get(String.format("%s.%s.%s", cdaVersion, dataSet, ehrMetaData));
    }

    public void setMetaData(String cdaVersion, String dataSet, String ehrMetaData,String val){
        set(String.format("%s.%s.%s", cdaVersion, dataSet, ehrMetaData),val);
    }
}
