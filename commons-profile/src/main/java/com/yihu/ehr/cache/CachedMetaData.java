package com.yihu.ehr.cache;

import java.io.Serializable;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.02 16:08
 */
public class CachedMetaData implements Serializable {
    public CachedMetaData(String innerCode, String type, long dictId){
        assert type.length() > 0;

        this.innerCode = innerCode;
        this.type = type;
        this.dictId = dictId;
    }

    public String innerCode;
    public String type;
    public long dictId;
}
