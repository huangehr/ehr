package com.yihu.ehr.std.model;

import java.util.Date;

/**
 * Created by AndyCai on 2015/12/17.
 */
public class DispatchLog {
    private String id;
    private String org_id;
    private String std_version_id;
    private Date dispatch_time;
    private String file_path;
    private String file_group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getStd_version_id() {
        return std_version_id;
    }

    public void setStd_version_id(String std_version_id) {
        this.std_version_id = std_version_id;
    }

    public Date getDispatch_time() {
        return dispatch_time;
    }

    public void setDispatch_time(Date dispatch_time) {
        this.dispatch_time = dispatch_time;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_group() {
        return file_group;
    }

    public void setFile_group(String file_group) {
        this.file_group = file_group;
    }
}
