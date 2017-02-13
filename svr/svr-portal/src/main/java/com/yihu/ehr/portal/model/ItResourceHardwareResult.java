package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "it_resource_hardware_result")
@Access(value = AccessType.FIELD)
public class ItResourceHardwareResult {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "master_id", unique = true, nullable = false)
    private int masterId;

    @Column(name = "detail_id", unique = true, nullable = false)
    private int detailId;

    @Column(name = "org_id", unique = true, nullable = false)
    private String orgId;

    @Column(name = "org_name", unique = true, nullable = false)
    private String orgName;

    @Column(name = "app_id", unique = true, nullable = false)
    private String appId;

    @Column(name = "app_name", unique = true, nullable = false)
    private String appName;

    @Column(name = "description", unique = true, nullable = false)
    private String description;

    @Column(name = "purpose", unique = true, nullable = false)
    private String purpose;

    @Column(name = "apply_user_id", unique = true, nullable = false)
    private String applyUserId;

    @Column(name = "apply_user_name", unique = true, nullable = false)
    private String applyUserName;

    @Column(name = "apply_phone", unique = true, nullable = false)
    private String applyPhone;

    @Column(name = "public_ip", unique = true, nullable = false)
    private String publicIp;

    @Column(name = "public_port", unique = true, nullable = false)
    private String publicPort;

    @Column(name = "private_ip", unique = true, nullable = false)
    private String privateIp;

    @Column(name = "private_port", unique = true, nullable = false)
    private String privatePort;

    @Column(name = "domain", unique = true, nullable = false)
    private String domain;

    @Column(name = "cpu", unique = true, nullable = false)
    private int cpu;

    @Column(name = "memory", unique = true, nullable = false)
    private int memory;

    @Column(name = "hardware", unique = true, nullable = false)
    private int hardware;

    @Column(name = "band_widty", unique = true, nullable = false)
    private int bandWidty;

    @Column(name = "system", unique = true, nullable = false)
    private String system;

    @Column(name = "pre_installed_software", unique = true, nullable = false)
    private String preInstalledSoftware;

    @Column(name = "login_code", unique = true, nullable = false)
    private String loginCode;

    @Column(name = "login_pw", unique = true, nullable = false)
    private String loginPw;

    @Column(name = "activity_flag", unique = true, nullable = false)
    private int activityFlag;

    @Column(name = "insert_time", unique = true, nullable = false)
    private Date insertTime;

    @Column(name = "remark", unique = true, nullable = false)
    private String remark;

    public ItResourceHardwareResult() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getApplyPhone() {
        return applyPhone;
    }

    public void setApplyPhone(String applyPhone) {
        this.applyPhone = applyPhone;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getPublicPort() {
        return publicPort;
    }

    public void setPublicPort(String publicPort) {
        this.publicPort = publicPort;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPrivatePort() {
        return privatePort;
    }

    public void setPrivatePort(String privatePort) {
        this.privatePort = privatePort;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getHardware() {
        return hardware;
    }

    public void setHardware(int hardware) {
        this.hardware = hardware;
    }

    public int getBandWidty() {
        return bandWidty;
    }

    public void setBandWidty(int bandWidty) {
        this.bandWidty = bandWidty;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getPreInstalledSoftware() {
        return preInstalledSoftware;
    }

    public void setPreInstalledSoftware(String preInstalledSoftware) {
        this.preInstalledSoftware = preInstalledSoftware;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getLoginPw() {
        return loginPw;
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw;
    }

    public int getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(int activityFlag) {
        this.activityFlag = activityFlag;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}