package com.yihu.ehr.analyze.service;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Airhead
 * @created 2016.01.16
 */
public class DataElementRecord {

    Map<String, String> dataGroup = new TreeMap<>();

    public void putMetaData(String metaDataCode, String value) {
        dataGroup.put(metaDataCode, value);
    }

    public Set<String> getMetaDataCodes() {
        return dataGroup.keySet();
    }

    public String getMetaData(String metaDataCode) {
        return dataGroup.get(metaDataCode);
    }

    public Map<String, String> getDataGroup() {
        return dataGroup;
    }

}
