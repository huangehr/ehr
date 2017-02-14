package com.yihu.ehr.portal.service;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "portal_doctor_shortcut", schema = "", catalog = "healtharchive")
public class PortalDoctorShortcut {
    private int id;
    private String userId;
    private String appId;
    private String workUri;
    private String workName;
    private Integer displayStatus;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = true, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "app_id", nullable = true, insertable = true, updatable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "work_uri", nullable = true, insertable = true, updatable = true)
    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }

    @Basic
    @Column(name = "work_name", nullable = true, insertable = true, updatable = true)
    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    @Basic
    @Column(name = "display_status", nullable = true, insertable = true, updatable = true)
    public Integer getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Integer displayStatus) {
        this.displayStatus = displayStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalDoctorShortcut that = (PortalDoctorShortcut) o;

        if (id != that.id) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (workUri != null ? !workUri.equals(that.workUri) : that.workUri != null) return false;
        if (workName != null ? !workName.equals(that.workName) : that.workName != null) return false;
        if (displayStatus != null ? !displayStatus.equals(that.displayStatus) : that.displayStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (workUri != null ? workUri.hashCode() : 0);
        result = 31 * result + (workName != null ? workName.hashCode() : 0);
        result = 31 * result + (displayStatus != null ? displayStatus.hashCode() : 0);
        return result;
    }
}
