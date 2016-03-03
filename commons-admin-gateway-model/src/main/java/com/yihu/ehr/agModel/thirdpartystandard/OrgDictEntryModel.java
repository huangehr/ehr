package com.yihu.ehr.agModel.thirdpartystandard;

/**
 * Created by AndyCai on 2016/3/2.
 */
public class OrgDictEntryModel {

    long id;
    String code;
    String name;
    Integer orgDict;
    String organization;
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

    public Integer getOrgDict() {
        return orgDict;
    }

    public void setOrgDict(Integer orgDict) {
        this.orgDict = orgDict;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
