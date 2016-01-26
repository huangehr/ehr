package com.yihu.ehr.dict.service;

import java.util.List;

/**
 * Created by cws on 2015/8/16.
 */
public class SystemDictModel {

    long id;
    String name;
    String reference;
    String authorId;
    String phoneticCode;
    String createDate;

    List<SystemDictEntryModel> systemDictEntryModels;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneticCode() {
        return phoneticCode;
    }

    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<SystemDictEntryModel> getSystemDictEntryModels() {
        return systemDictEntryModels;
    }

    public void setSystemDictEntryModels(List<SystemDictEntryModel> systemDictEntryModels) {
        this.systemDictEntryModels = systemDictEntryModels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
