package com.yihu.ehr.dao.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Entity
@Table(name = "sc_archive_visit_log")
@Access(value = AccessType.PROPERTY)
public class ArchiveVisitLog {

    long id;
    String identityId;
    String visitorId;
    String visitorName;
    String appId;
    String rowKey;
    Date visitTime;

    public ArchiveVisitLog() {
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

    @Column(name = "identity_id", nullable = false)
    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    @Column(name = "visitor_id", nullable = false)
    public String getVisitorId() {
        return visitorId;
    }
    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    @Column(name = "visitor_name", nullable = false)
    public String getVisitorName() {
        return visitorName;
    }
    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    @Column(name = "app_id", nullable = false)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "row_key", nullable = false)
    public String getRowKey() {
        return rowKey;
    }
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    @Column(name = "visit_time", nullable = false)
    public Date getVisitTime() {
        return visitTime;
    }
    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

}