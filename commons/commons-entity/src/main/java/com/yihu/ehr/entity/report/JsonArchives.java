package com.yihu.ehr.entity.report;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * JSON档案包。
 *
 */
@Entity
@Table(name = "json_archives")
public class JsonArchives {

    public enum ArchiveStatus {
        // 0 已缓存
        Received,
        // 1 正在入库
        Acquired,
        // 2 入库失败
        Failed,
        // 3 已入库
        Finished
        // 4 未能入库的档案
        //LegacyIgnored;
    }

    private String id;
    private String pwd;
    private String remotePath;
    private String message;
    private String md5;
    private String orgCode;
    private String clientId;                            // 档案应用来源
    private Date receiveDate;
    private Date parseDate;
    private Date finishDate;
    private boolean resourced;                          // 是否已经资源化处理
    private ArchiveStatus archiveStatus;
    private String eventType;//就诊事件类型 0门诊 1住院 2体检',
    private String eventNo;//就诊事件no',
    private Date eventDate;//就诊时间',
    private String patientId;//医院患者ID'

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "pwd", nullable = false)
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Column(name = "remote_path")
    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    @Column(name = "receive_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date date) {
        receiveDate = date;
    }

    @Column(name = "parse_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getParseDate() {
        return parseDate;
    }

    public void setParseDate(Date date) {
        this.parseDate = date;
    }

    @Column(name = "finish_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date date) {
        this.finishDate = date;
    }

    @Column(name = "archive_status")
    public ArchiveStatus getArchiveStatus() {
        return archiveStatus;
    }

    public void setArchiveStatus(ArchiveStatus archiveStatus) {
        this.archiveStatus = archiveStatus;
    }

    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "md5_value")
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Column(name = "resourced")
    public boolean isResourced() {
        return resourced;
    }

    public void setResourced(boolean resourced) {
        this.resourced = resourced;
    }

    @Column(name = "event_type")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Column(name = "event_no")
    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    @Column(name = "event_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Column(name = "patient_id")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
