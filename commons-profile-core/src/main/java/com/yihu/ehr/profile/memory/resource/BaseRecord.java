package com.yihu.ehr.profile.memory.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.16 16:10
 */
public class BaseRecord {
    protected Map<String, String> dataGroup = new HashMap<>();

    public void addResource(String resourceCode, String value){
        dataGroup.put(resourceCode, value);
    }
}
