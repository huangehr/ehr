package com.yihu.ehr.agModel.thirdpartystandard;

/**
 * Created by AndyCai on 2016/3/1.
 */
public class OrgMetaDataModel {
    long id;
    String code;
    String name;
    int orgDataSet;
    int sequence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrgDataSet() {
        return orgDataSet;
    }

    public void setOrgDataSet(int orgDataSet) {
        this.orgDataSet = orgDataSet;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
