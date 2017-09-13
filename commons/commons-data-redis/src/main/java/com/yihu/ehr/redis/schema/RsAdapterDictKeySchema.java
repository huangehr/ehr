package com.yihu.ehr.redis.schema;

import com.yihu.ehr.redis.KeySchema;
import org.springframework.stereotype.Service;

/**
 * Created by hzp on 2017.04.25
 */
@Service
public class RsAdapterDictKeySchema extends KeySchema {

    public RsAdapterDictKeySchema(){
        super.table="rs_adapter_dictionary";
        super.column="code_name";
    }


    public String getMetaData(String cdaVersion, String dictCode, String srcDictEntryCode){
        return get(String.format("%s.%s.%s", cdaVersion, dictCode, srcDictEntryCode));
    }

    public void setMetaData(String cdaVersion, String dictCode, String srcDictEntryCode,String val){
        set(String.format("%s.%s.%s", cdaVersion, dictCode, srcDictEntryCode),val);
    }
}
