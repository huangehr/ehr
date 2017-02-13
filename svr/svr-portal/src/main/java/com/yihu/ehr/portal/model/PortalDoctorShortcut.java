package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "portal_doctor_shortcut")
@Access(value = AccessType.FIELD)
public class PortalDoctorShortcut {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "app_id", unique = true, nullable = false)
    private String appId;

    @Column(name = "work_uri", unique = true, nullable = false)
    private String workUri;

    @Column(name = "work_name", unique = true, nullable = false)
    private String workName;

    @Column(name = "display_status", unique = true, nullable = false)
    private int displayStatus;

    public PortalDoctorShortcut() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getWorkUri() {
        return workUri;
    }

    public void setWorkUri(String workUri) {
        this.workUri = workUri;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public int getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(int displayStatus) {
        this.displayStatus = displayStatus;
    }
}