package com.yihu.ehr.model.pk;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Administrator on 2016/1/11.
 */
public class tablePk implements Serializable{
    String code;
    long dictId;

    public tablePk(){
    }

    public tablePk(String code, Long dictId){
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
        if(obj instanceof DictPk){
            DictPk pk = (DictPk)obj;
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
