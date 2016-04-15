package com.yihu.ehr.profile.core;

import java.util.Date;
import java.util.List;

/**
 * 非结构化健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class NonstructedProfile extends Profile {
    protected Date expireDate;

    protected List<ProfileDocument> documents;

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public List<ProfileDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ProfileDocument> documents) {
        this.documents = documents;
    }

    public ProfileType getType(){
        return ProfileType.Nonstructed;
    }
}
