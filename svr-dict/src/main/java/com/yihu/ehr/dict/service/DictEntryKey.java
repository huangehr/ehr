package com.yihu.ehr.dict.service;

import java.io.Serializable;
import java.util.Objects;

/**
 * 字典项主键（联合主键）。
 */
public class DictEntryKey implements Serializable{
    String code;
    long dictId;

    public DictEntryKey(){
    }

    public DictEntryKey(String code, Long dictId){
        this.code = code == null ? "" : code;
        this.dictId = dictId == null ? null : dictId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getDictId() {
        return dictId;
    }

    public void setDictId(long dictId) {
        this.dictId = dictId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DictEntryKey){
            DictEntryKey pk = (DictEntryKey)obj;
            if(this.code==pk.getCode() && this.dictId==(pk.getDictId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, dictId);
    }


}
