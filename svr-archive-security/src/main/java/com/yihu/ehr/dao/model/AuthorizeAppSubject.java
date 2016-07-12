package com.yihu.ehr.dao.model;

import javax.persistence.*;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Entity
@Table(name = "sc_authorize_app_subject")
@Access(value = AccessType.PROPERTY)
public class AuthorizeAppSubject {

    long id;
    String appAuthorizeId;
    String subjectId;
    int status;

    public AuthorizeAppSubject() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "app_authorize_id", nullable = false)
    public String getAppAuthorizeId() {
        return appAuthorizeId;
    }
    public void setAppAuthorizeId(String appAuthorizeId) {
        this.appAuthorizeId = appAuthorizeId;
    }

    @Column(name = "subject_id", nullable = false)
    public String getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Column(name = "status", nullable = false)
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}