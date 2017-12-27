package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by janseny on 2017/12/26.
 */
public class SaveModelOrgHealthCategory {
    @JestId
    private String id;
    private String quotaCode;//指标code
    private String quotaName;//指标名称
    private String quotaDate;//统计的指标对应时间 如今天凌晨统计昨天的数据，那就是昨天的时间
    private String orgHealthCategoryCode;     //卫生机构类型代码
    private String orgHealthCategoryName;   //卫生机构类型名字
    private String orgHealthCategoryId;
    private String orgHealthCategoryPid;
    private String town;    //区县代码
    private String townName;//区县名称
    private String year;
    private String yearName;
    private String result;//统计结果
    private String slaveKey1;//从维度  1级维度
    private String slaveKey1Name;
    private String slaveKey2;//从维度  2级维度
    private String slaveKey2Name;
    private String slaveKey3;//从维度  3级维度
    private String slaveKey3Name;
    private String slaveKey4;//从维度  4级维度
    private String slaveKey4Name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
    @CreatedDate
    private Date createTime;//创建时间

    public String getQuotaDate() {
        return quotaDate;
    }

    public void setQuotaDate(String quotaDate) {
        this.quotaDate = quotaDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSlaveKey1() {
        return slaveKey1;
    }

    public void setSlaveKey1(String slaveKey1) {
        this.slaveKey1 = slaveKey1;
    }

    public String getSlaveKey1Name() {
        return slaveKey1Name;
    }

    public void setSlaveKey1Name(String slaveKey1Name) {
        this.slaveKey1Name = slaveKey1Name;
    }

    public String getSlaveKey2() {
        return slaveKey2;
    }

    public void setSlaveKey2(String slaveKey2) {
        this.slaveKey2 = slaveKey2;
    }

    public String getSlaveKey2Name() {
        return slaveKey2Name;
    }

    public void setSlaveKey2Name(String slaveKey2Name) {
        this.slaveKey2Name = slaveKey2Name;
    }

    public String getSlaveKey3() {
        return slaveKey3;
    }

    public void setSlaveKey3(String slaveKey3) {
        this.slaveKey3 = slaveKey3;
    }

    public String getSlaveKey3Name() {
        return slaveKey3Name;
    }

    public void setSlaveKey3Name(String slaveKey3Name) {
        this.slaveKey3Name = slaveKey3Name;
    }

    public String getSlaveKey4() {
        return slaveKey4;
    }

    public void setSlaveKey4(String slaveKey4) {
        this.slaveKey4 = slaveKey4;
    }

    public String getSlaveKey4Name() {
        return slaveKey4Name;
    }

    public void setSlaveKey4Name(String slaveKey4Name) {
        this.slaveKey4Name = slaveKey4Name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
