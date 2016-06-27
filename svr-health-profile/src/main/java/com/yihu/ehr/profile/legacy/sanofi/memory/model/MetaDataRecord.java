package com.yihu.ehr.profile.legacy.sanofi.memory.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Sand
 * @created 2016.04.26 18:55
 */
public class MetaDataRecord {
    Map<String, String> dataGroup = new TreeMap<>();

    public Map<String, String> getDataGroup(){
        return dataGroup;
    }

    public Set<String> getMetaDataCodes(){
        return dataGroup.keySet();
    }

    public String getMetaData(String metaDataCode){
        return dataGroup.get(metaDataCode);
    }

    public void putMetaData(String metaDataCode, String value){
        dataGroup.put(metaDataCode, value);
    }
}
