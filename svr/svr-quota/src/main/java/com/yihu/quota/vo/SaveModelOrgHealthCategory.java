package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by janseny on 2017/12/26.
 */
public class SaveModelOrgHealthCategory{
    @JestId
    private String id;
    private String orgHealthCategoryQuotaCode;//指标code
    private String orgHealthCategoryQuotaName;//指标名称
    private String orgHealthCategoryQuotaDate;//统计的指标对应时间 如今天凌晨统计昨天的数据，那就是昨天的时间
    private String orgHealthCategoryCode;     //卫生机构类型代码
    private String orgHealthCategoryName;   //卫生机构类型名字
    private String orgHealthCategoryId;
    private String orgHealthCategoryPid;
    private String orgHealthCategoryTown;    //区县代码
    private String orgHealthCategoryTownName;//区县名称
    private String orgHealthCategoryYear;
    private String orgHealthCategoryYearName;
    private String orgHealthCategoryResult;//统计结果
    private String orgHealthCategorySlaveKey1;//从维度  1级维度
    private String orgHealthCategorySlaveKey1Name;
    private String orgHealthCategorySlaveKey2;//从维度  2级维度
    private String orgHealthCategorySlaveKey2Name;
    private String orgHealthCategorySlaveKey3;//从维度  3级维度
    private String orgHealthCategorySlaveKey3Name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
    @CreatedDate
    private Date orgHealthCategorySCreateTime;//创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgHealthCategoryQuotaCode() {
        return orgHealthCategoryQuotaCode;
    }

    public void setOrgHealthCategoryQuotaCode(String orgHealthCategoryQuotaCode) {
        this.orgHealthCategoryQuotaCode = orgHealthCategoryQuotaCode;
    }

    public String getOrgHealthCategoryQuotaName() {
        return orgHealthCategoryQuotaName;
    }

    public void setOrgHealthCategoryQuotaName(String orgHealthCategoryQuotaName) {
        this.orgHealthCategoryQuotaName = orgHealthCategoryQuotaName;
    }

    public String getOrgHealthCategoryQuotaDate() {
        return orgHealthCategoryQuotaDate;
    }

    public void setOrgHealthCategoryQuotaDate(String orgHealthCategoryQuotaDate) {
        this.orgHealthCategoryQuotaDate = orgHealthCategoryQuotaDate;
    }

    public String getOrgHealthCategoryCode() {
        return orgHealthCategoryCode;
    }

    public void setOrgHealthCategoryCode(String orgHealthCategoryCode) {
        this.orgHealthCategoryCode = orgHealthCategoryCode;
    }

    public String getOrgHealthCategoryName() {
        return orgHealthCategoryName;
    }

    public void setOrgHealthCategoryName(String orgHealthCategoryName) {
        this.orgHealthCategoryName = orgHealthCategoryName;
    }

    public String getOrgHealthCategoryId() {
        return orgHealthCategoryId;
    }

    public void setOrgHealthCategoryId(String orgHealthCategoryId) {
        this.orgHealthCategoryId = orgHealthCategoryId;
    }

    public String getOrgHealthCategoryPid() {
        return orgHealthCategoryPid;
    }

    public void setOrgHealthCategoryPid(String orgHealthCategoryPid) {
        this.orgHealthCategoryPid = orgHealthCategoryPid;
    }

    public String getOrgHealthCategoryTown() {
        return orgHealthCategoryTown;
    }

    public void setOrgHealthCategoryTown(String orgHealthCategoryTown) {
        this.orgHealthCategoryTown = orgHealthCategoryTown;
    }

    public String getOrgHealthCategoryTownName() {
        return orgHealthCategoryTownName;
    }

    public void setOrgHealthCategoryTownName(String orgHealthCategoryTownName) {
        this.orgHealthCategoryTownName = orgHealthCategoryTownName;
    }

    public String getOrgHealthCategoryYear() {
        return orgHealthCategoryYear;
    }

    public void setOrgHealthCategoryYear(String orgHealthCategoryYear) {
        this.orgHealthCategoryYear = orgHealthCategoryYear;
    }

    public String getOrgHealthCategoryYearName() {
        return orgHealthCategoryYearName;
    }

    public void setOrgHealthCategoryYearName(String orgHealthCategoryYearName) {
        this.orgHealthCategoryYearName = orgHealthCategoryYearName;
    }

    public String getOrgHealthCategoryResult() {
        return orgHealthCategoryResult;
    }

    public void setOrgHealthCategoryResult(String orgHealthCategoryResult) {
        this.orgHealthCategoryResult = orgHealthCategoryResult;
    }

    public String getOrgHealthCategorySlaveKey1() {
        return orgHealthCategorySlaveKey1;
    }

    public void setOrgHealthCategorySlaveKey1(String orgHealthCategorySlaveKey1) {
        this.orgHealthCategorySlaveKey1 = orgHealthCategorySlaveKey1;
    }

    public String getOrgHealthCategorySlaveKey1Name() {
        return orgHealthCategorySlaveKey1Name;
    }

    public void setOrgHealthCategorySlaveKey1Name(String orgHealthCategorySlaveKey1Name) {
        this.orgHealthCategorySlaveKey1Name = orgHealthCategorySlaveKey1Name;
    }

    public String getOrgHealthCategorySlaveKey2() {
        return orgHealthCategorySlaveKey2;
    }

    public void setOrgHealthCategorySlaveKey2(String orgHealthCategorySlaveKey2) {
        this.orgHealthCategorySlaveKey2 = orgHealthCategorySlaveKey2;
    }

    public String getOrgHealthCategorySlaveKey2Name() {
        return orgHealthCategorySlaveKey2Name;
    }

    public void setOrgHealthCategorySlaveKey2Name(String orgHealthCategorySlaveKey2Name) {
        this.orgHealthCategorySlaveKey2Name = orgHealthCategorySlaveKey2Name;
    }

    public String getOrgHealthCategorySlaveKey3() {
        return orgHealthCategorySlaveKey3;
    }

    public void setOrgHealthCategorySlaveKey3(String orgHealthCategorySlaveKey3) {
        this.orgHealthCategorySlaveKey3 = orgHealthCategorySlaveKey3;
    }

    public String getOrgHealthCategorySlaveKey3Name() {
        return orgHealthCategorySlaveKey3Name;
    }

    public void setOrgHealthCategorySlaveKey3Name(String orgHealthCategorySlaveKey3Name) {
        this.orgHealthCategorySlaveKey3Name = orgHealthCategorySlaveKey3Name;
    }

    public Date getOrgHealthCategorySCreateTime() {
        return orgHealthCategorySCreateTime;
    }

    public void setOrgHealthCategorySCreateTime(Date orgHealthCategorySCreateTime) {
        this.orgHealthCategorySCreateTime = orgHealthCategorySCreateTime;
    }
}
