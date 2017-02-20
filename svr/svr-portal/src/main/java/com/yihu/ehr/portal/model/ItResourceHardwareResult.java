package com.yihu.ehr.portal.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "it_resource_hardware_result", schema = "", catalog = "healtharchive")
public class ItResourceHardwareResult {
    private int id;
    private Integer masterId;
    private Integer detailId;
    private String orgId;
    private String orgName;
    private String appId;
    private String appName;
    private String description;
    private String purpose;
    private String applyUserId;
    private String applyUserName;
    private String applyPhone;
    private String publicIp;
    private String publicPort;
    private String privateIp;
    private String privatePort;
    private String domain;
    private Integer cpu;
    private Integer memory;
    private Integer hardware;
    private Integer bandWidty;
    private String system;
    private String preInstalledSoftware;
    private String loginCode;
    private String loginPw;
    private Integer activityFlag;
    private Timestamp insertTime;
    private String remark;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "master_id", nullable = true, insertable = true, updatable = true)
    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    @Basic
    @Column(name = "detail_id", nullable = true, insertable = true, updatable = true)
    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    @Basic
    @Column(name = "org_id", nullable = true, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Basic
    @Column(name = "org_name", nullable = true, insertable = true, updatable = true)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "app_id", nullable = true, insertable = true, updatable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "app_name", nullable = true, insertable = true, updatable = true)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "purpose", nullable = true, insertable = true, updatable = true)
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Basic
    @Column(name = "apply_user_id", nullable = true, insertable = true, updatable = true)
    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    @Basic
    @Column(name = "apply_user_name", nullable = true, insertable = true, updatable = true)
    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    @Basic
    @Column(name = "apply_phone", nullable = true, insertable = true, updatable = true)
    public String getApplyPhone() {
        return applyPhone;
    }

    public void setApplyPhone(String applyPhone) {
        this.applyPhone = applyPhone;
    }

    @Basic
    @Column(name = "public_ip", nullable = true, insertable = true, updatable = true)
    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    @Basic
    @Column(name = "public_port", nullable = true, insertable = true, updatable = true)
    public String getPublicPort() {
        return publicPort;
    }

    public void setPublicPort(String publicPort) {
        this.publicPort = publicPort;
    }

    @Basic
    @Column(name = "private_ip", nullable = true, insertable = true, updatable = true)
    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    @Basic
    @Column(name = "private_port", nullable = true, insertable = true, updatable = true)
    public String getPrivatePort() {
        return privatePort;
    }

    public void setPrivatePort(String privatePort) {
        this.privatePort = privatePort;
    }

    @Basic
    @Column(name = "domain", nullable = true, insertable = true, updatable = true)
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Basic
    @Column(name = "cpu", nullable = true, insertable = true, updatable = true)
    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    @Basic
    @Column(name = "memory", nullable = true, insertable = true, updatable = true)
    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    @Basic
    @Column(name = "hardware", nullable = true, insertable = true, updatable = true)
    public Integer getHardware() {
        return hardware;
    }

    public void setHardware(Integer hardware) {
        this.hardware = hardware;
    }

    @Basic
    @Column(name = "band_widty", nullable = true, insertable = true, updatable = true)
    public Integer getBandWidty() {
        return bandWidty;
    }

    public void setBandWidty(Integer bandWidty) {
        this.bandWidty = bandWidty;
    }

    @Basic
    @Column(name = "system", nullable = true, insertable = true, updatable = true)
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Basic
    @Column(name = "pre_installed_software", nullable = true, insertable = true, updatable = true)
    public String getPreInstalledSoftware() {
        return preInstalledSoftware;
    }

    public void setPreInstalledSoftware(String preInstalledSoftware) {
        this.preInstalledSoftware = preInstalledSoftware;
    }

    @Basic
    @Column(name = "login_code", nullable = true, insertable = true, updatable = true)
    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    @Basic
    @Column(name = "login_pw", nullable = true, insertable = true, updatable = true)
    public String getLoginPw() {
        return loginPw;
    }

    public void setLoginPw(String loginPw) {
        this.loginPw = loginPw;
    }

    @Basic
    @Column(name = "activity_flag", nullable = true, insertable = true, updatable = true)
    public Integer getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(Integer activityFlag) {
        this.activityFlag = activityFlag;
    }

    @Basic
    @Column(name = "insert_time", nullable = true, insertable = true, updatable = true)
    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "remark", nullable = true, insertable = true, updatable = true)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItResourceHardwareResult that = (ItResourceHardwareResult) o;

        if (id != that.id) return false;
        if (masterId != null ? !masterId.equals(that.masterId) : that.masterId != null) return false;
        if (detailId != null ? !detailId.equals(that.detailId) : that.detailId != null) return false;
        if (orgId != null ? !orgId.equals(that.orgId) : that.orgId != null) return false;
        if (orgName != null ? !orgName.equals(that.orgName) : that.orgName != null) return false;
        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (appName != null ? !appName.equals(that.appName) : that.appName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (applyUserId != null ? !applyUserId.equals(that.applyUserId) : that.applyUserId != null) return false;
        if (applyUserName != null ? !applyUserName.equals(that.applyUserName) : that.applyUserName != null)
            return false;
        if (applyPhone != null ? !applyPhone.equals(that.applyPhone) : that.applyPhone != null) return false;
        if (publicIp != null ? !publicIp.equals(that.publicIp) : that.publicIp != null) return false;
        if (publicPort != null ? !publicPort.equals(that.publicPort) : that.publicPort != null) return false;
        if (privateIp != null ? !privateIp.equals(that.privateIp) : that.privateIp != null) return false;
        if (privatePort != null ? !privatePort.equals(that.privatePort) : that.privatePort != null) return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (cpu != null ? !cpu.equals(that.cpu) : that.cpu != null) return false;
        if (memory != null ? !memory.equals(that.memory) : that.memory != null) return false;
        if (hardware != null ? !hardware.equals(that.hardware) : that.hardware != null) return false;
        if (bandWidty != null ? !bandWidty.equals(that.bandWidty) : that.bandWidty != null) return false;
        if (system != null ? !system.equals(that.system) : that.system != null) return false;
        if (preInstalledSoftware != null ? !preInstalledSoftware.equals(that.preInstalledSoftware) : that.preInstalledSoftware != null)
            return false;
        if (loginCode != null ? !loginCode.equals(that.loginCode) : that.loginCode != null) return false;
        if (loginPw != null ? !loginPw.equals(that.loginPw) : that.loginPw != null) return false;
        if (activityFlag != null ? !activityFlag.equals(that.activityFlag) : that.activityFlag != null) return false;
        if (insertTime != null ? !insertTime.equals(that.insertTime) : that.insertTime != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (masterId != null ? masterId.hashCode() : 0);
        result = 31 * result + (detailId != null ? detailId.hashCode() : 0);
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (applyUserId != null ? applyUserId.hashCode() : 0);
        result = 31 * result + (applyUserName != null ? applyUserName.hashCode() : 0);
        result = 31 * result + (applyPhone != null ? applyPhone.hashCode() : 0);
        result = 31 * result + (publicIp != null ? publicIp.hashCode() : 0);
        result = 31 * result + (publicPort != null ? publicPort.hashCode() : 0);
        result = 31 * result + (privateIp != null ? privateIp.hashCode() : 0);
        result = 31 * result + (privatePort != null ? privatePort.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (cpu != null ? cpu.hashCode() : 0);
        result = 31 * result + (memory != null ? memory.hashCode() : 0);
        result = 31 * result + (hardware != null ? hardware.hashCode() : 0);
        result = 31 * result + (bandWidty != null ? bandWidty.hashCode() : 0);
        result = 31 * result + (system != null ? system.hashCode() : 0);
        result = 31 * result + (preInstalledSoftware != null ? preInstalledSoftware.hashCode() : 0);
        result = 31 * result + (loginCode != null ? loginCode.hashCode() : 0);
        result = 31 * result + (loginPw != null ? loginPw.hashCode() : 0);
        result = 31 * result + (activityFlag != null ? activityFlag.hashCode() : 0);
        result = 31 * result + (insertTime != null ? insertTime.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        return result;
    }
}
