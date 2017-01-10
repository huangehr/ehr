package com.yihu.ehr.model.archivesecurity;

/**
 * Created by lyr on 2016/7/11.
 */
public class MRsAuthorizeSubjectResource {

    private String id;
    private String subjectId;
    private String resourceId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
