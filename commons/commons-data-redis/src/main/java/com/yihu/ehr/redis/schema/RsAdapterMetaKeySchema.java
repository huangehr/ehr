package com.yihu.ehr.redis.schema;

import org.springframework.stereotype.Service;

/**
 * Created by hzp on 2017.04.25
 */
@Service
public class RsAdapterMetaKeySchema extends KeySchema {

    public RsAdapterMetaKeySchema(){
        super.table="rs_adapter_metadata";
        super.column="resource_metadata";
    }

    public String getMetaData(String cdaVersion, String dataSet, String ehrMetaData){
        return get(String.format("%s.%s.%s", cdaVersion, dataSet, ehrMetaData));
    }

    public void setMetaData(String cdaVersion, String dataSet, String ehrMetaData, String val){
        set(String.format("%s.%s.%s", cdaVersion, dataSet, ehrMetaData), val);
    }

    public void deleteVersion (String cdaVersion) {
        delete(String.format("%s.%s.%s", cdaVersion, "*", "*"));
    }
}
