package com.yihu.ehr.pack.service;

import com.yihu.ehr.constrant.ArchiveStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * JSON档案包。
 *
 * @author Air
 * @version 1.0
 * @created 2015.07.09 15:08
 */
@Entity
@Table(name = "json_archives")
@Access(value = AccessType.PROPERTY)
public class JsonPackage {
    public final static String pathSeparator = ":";     // 统一使用冒号作为路径分隔符

    private String id;
    private String pwd;
    private String remotePath;
    private String message;
    private Date receiveDate;
    private Date parseDate;
    private Date finishDate;
    private ArchiveStatus archiveStatus;

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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonPackage that = (JsonPackage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (pwd != null ? !pwd.equals(that.pwd) : that.pwd != null) return false;
        if (remotePath != null ? !remotePath.equals(that.remotePath) : that.remotePath != null) return false;

        return true;
    }

    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (pwd != null ? pwd.hashCode() : 0);
        result = 31 * result + (remotePath != null ? remotePath.hashCode() : 0);
        return result;
    }
}
