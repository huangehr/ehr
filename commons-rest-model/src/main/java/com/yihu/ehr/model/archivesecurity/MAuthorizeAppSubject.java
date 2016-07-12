package com.yihu.ehr.model.archivesecurity;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public class MAuthorizeAppSubject {

    long id;
    String appAuthorizeId;
    String subjectId;
    int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppAuthorizeId() {
        return appAuthorizeId;
    }

    public void setAppAuthorizeId(String appAuthorizeId) {
        this.appAuthorizeId = appAuthorizeId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}