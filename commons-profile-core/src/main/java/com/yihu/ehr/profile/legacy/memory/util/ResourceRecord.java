package com.yihu.ehr.profile.legacy.memory.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 资源记录。
 *
 * @author Sand
 * @created 2016.05.16 16:10
 */
public class ResourceRecord {
    protected Map<String, String> dataGroup = new HashMap<>();

    public void addResource(String resourceCode, String value){
        dataGroup.put(resourceCode, value);
    }

    public Set<String> resourceCodes(){
        return dataGroup.keySet();
    }

    public String getResourceValue(String resourceCode){
        return dataGroup.get(resourceCode);
    }

    public Map<String, String> getDataGroup(){
        return dataGroup;
    }
}
