package com.yihu.ehr.entity.quality;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据质量-平台资源化预警值
 * @author yeshijie on 2018/5/28.
 */
@Entity
@Table(name = "dq_paltform_resource_warning", schema = "", catalog = "healtharchive")
public class DqPaltformResourceWarning {

    private Long id;
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private String updateUserId;//操作人id
    private String updateUserName;//操作人名称
    private Long failureNum;//失败数
    private Long errorNum;//质量问题数
    private Long unparsingNum;//未解析量
    private Long unparsingPeriod;//未解析量预警周期

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "update_user_id")
    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Column(name = "update_user_name")
    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    @Column(name = "failure_num")
    public Long getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(Long failureNum) {
        this.failureNum = failureNum;
    }

    @Column(name = "error_num")
    public Long getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Long errorNum) {
        this.errorNum = errorNum;
    }

    @Column(name = "unparsing_num")
    public Long getUnparsingNum() {
        return unparsingNum;
    }

    public void setUnparsingNum(Long unparsingNum) {
        this.unparsingNum = unparsingNum;
    }

    @Column(name = "unparsing_period")
    public Long getUnparsingPeriod() {
        return unparsingPeriod;
    }

    public void setUnparsingPeriod(Long unparsingPeriod) {
        this.unparsingPeriod = unparsingPeriod;
    }
}
