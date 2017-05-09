package com.yihu.ehr.pack.service;

import com.yihu.ehr.constants.ArchiveStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * JSON档案包。
 *
 * 2016.04.25 增加来源机构，来源应用，档案md5及是否被资源化
 *
 * @author Sand
 * @created 2015.07.09 15:08
 */
@Entity
@Table(name = "json_archives")
@Access(value = AccessType.PROPERTY)
public class Package {
    public final static String pathSeparator = ":";     // 使用冒号作为路径分隔符，与Linux一致

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
    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date date) {
        receiveDate = date;
    }

    @Column(name = "parse_date")
    public Date getParseDate() {
        return parseDate;
    }

    public void setParseDate(Date date) {
        this.parseDate = date;
    }

    @Column(name = "finish_date")
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
