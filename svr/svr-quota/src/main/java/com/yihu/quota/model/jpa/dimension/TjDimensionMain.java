package com.yihu.quota.model.jpa.dimension;// default package

import java.util.Date;
import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TjDimensionMain entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tj_dimension_main")
public class TjDimensionMain implements java.io.Serializable {

    // Fields

    private Integer id;
    private String code;
    private String name;
    private Date createTime;
    private String createUser;
    private String createUserName;
    private Date updateTime;
    private String updateUser;
    private String updateUserName;
    private Integer status;//1: 正常 0：不可以用 -1 已删除
    private String remark;
    /**
     * 主维度 :
     * 1 时间维度(日)
     * 2 时间维度(周)
     * 3.时间维度(月)
     * 4时间维度(年)
     * 5 行政区划维度（省）
     * 6行政区划维度（市）
     * 7行政区划维度（区县）
     * 8行政区划维度（机构）
     * 9行政区划维度（团队）
     */
    private String type;

    // Constructors

    /**
     * default constructor
     */
    public TjDimensionMain() {
    }

    /**
     * minimal constructor
     */
    public TjDimensionMain(Date createTime, Date updateTime) {
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * full constructor
     */
    public TjDimensionMain(String code, String name, Date createTime,
                           String createUser, String createUserName, Date updateTime,
                           String updateUser, String updateUserName, Integer status,
                           String remark, String type) {
        this.code = code;
        this.name = name;
        this.createTime = createTime;
        this.createUser = createUser;
        this.createUserName = createUserName;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.updateUserName = updateUserName;
        this.status = status;
        this.remark = remark;
        this.type = type;
    }

    // Property accessors
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "code", length = 100)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", length = 200)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 0)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "create_user", length = 100)
    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(name = "create_user_name", length = 50)
    public String getCreateUserName() {
        return this.createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable = false, length = 0)
    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "update_user", length = 100)
    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "update_user_name", length = 50)
    public String getUpdateUserName() {
        return this.updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    @Column(name = "status")
    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "remark", length = 1500)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "type", length = 10)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}