package com.yihu.ehr.profile.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Sand
 * @created 2016.04.26 18:55
 */
public class DataRecord {
    Map<String, String> metaDataGroup;

    public Map<String, String> getMetaDataGroup(){
        return metaDataGroup;
    }

    public Set<String> getMetaDataCodes(){
        return metaDataGroup.keySet();
    }

    public String getMetaData(String metaDataCode){
        return metaDataGroup.get(metaDataCode);
    }

    public void putMetaData(String metaDataCode, String value){
        metaDataGroup.put(metaDataCode, value);
    }
}
