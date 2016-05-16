package com.yihu.ehr.api.esb.model;// default package

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * HosAcqTask entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hos_acq_task")
@Access(value = AccessType.PROPERTY)
public class HosAcqTask implements java.io.Serializable {


    // Fields    

    private String id;
    private String orgCode;
    private String systemCode;
    private Date startTime;
    private Date endTime;
    private String status;
    private String message;
    private Date createTime;


    @Id
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    @Column(name = "id", unique = true, nullable = false, length = 64)

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "org_code", length = 64)
    public String getOrgCode() {
        return this.orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "system_code", length = 64)
    public String getSystemCode() {
        return this.systemCode;
    }
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Column(name = "start_time", length = 0)
    public Date getStartTime() {
        return this.startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "end_time", length = 0)
    public Date getEndTime() {
        return this.endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "status", length = 10)
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "message", length = 2000)
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "create_time", length = 0)
    public Date getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


}