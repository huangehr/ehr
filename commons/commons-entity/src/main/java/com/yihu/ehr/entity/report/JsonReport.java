package com.yihu.ehr.entity.report;

import com.yihu.ehr.constants.ArchiveStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * JSON日报包。
 *
 */
@Entity
@Table(name = "json_report")
public class JsonReport {

    public final static String pathSeparator = ":";

    private Integer id;
    private String pwd;
    private String remotePath;
    private Date receiveDate;
    private Date parseDate;
    private Date statisDate;
    private Date finishDate;
    private String message;
    private String md5;
    private String encryptPwd;
    private String orgCode;
    private Integer type;     // 1 质控包 2 日报包
    private Integer status;     //  0 未解析  1 已解析 2 已统计



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Column(name = "encrypt_pwd")
    public String getEncryptPwd() {
        return encryptPwd;
    }

    public void setEncryptPwd(String encryptPwd) {
        this.encryptPwd = encryptPwd;
    }

    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "statis_date")
    public Date getStatisDate() {
        return statisDate;
    }

    public void setStatisDate(Date statisDate) {
        this.statisDate = statisDate;
    }
}