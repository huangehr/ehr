package com.yihu.ehr.api.esb.model;// default package

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * HosSqlTask entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hos_sql_task")
public class HosSqlTask implements java.io.Serializable {


    // Fields    

    private String id;
    private String orgCode;
    private String systemCode;
    private String sql;
    private String result;
    private String status;
    private String message;
    private Timestamp createTime;


    // Constructors

    /**
     * default constructor
     */
    public HosSqlTask() {
    }

    /**
     * minimal constructor
     */
    public HosSqlTask(String id) {
        this.id = id;
    }

    /**
     * full constructor
     */
    public HosSqlTask(String id, String orgCode, String systemCode, String sql, String result, String status, String message, Timestamp createTime) {
        this.id = id;
        this.orgCode = orgCode;
        this.systemCode = systemCode;
        this.sql = sql;
        this.result = result;
        this.status = status;
        this.message = message;
        this.createTime = createTime;
    }


    // Property accessors
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

    @Column(name = "sql", length = 2000)

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Column(name = "result", length = 2000)

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Column(name = "status", length = 10)

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "message", length = 200)

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "create_time", length = 0)

    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }


}