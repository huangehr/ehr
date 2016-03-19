package com.yihu.ehr.cache;

import java.io.Serializable;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.19 10:53
 */
public class CachedDataSet implements Serializable {
    public CachedDataSet(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String code;
    public String name;
}
