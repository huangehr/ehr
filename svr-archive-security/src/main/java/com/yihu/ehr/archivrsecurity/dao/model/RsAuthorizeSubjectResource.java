package com.yihu.ehr.archivrsecurity.dao.model;

import javax.persistence.*;

/**
 * Created by lyr on 2016/7/11.
 */
@Entity
@Table(name="rs_authorize_subject_resource")
public class RsAuthorizeSubjectResource {

    private String id;
    private String subjectId;
    private String resourceId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",nullable = false,unique = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="subject_id",nullable = false)
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Column(name="resource_id",nullable = false)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
