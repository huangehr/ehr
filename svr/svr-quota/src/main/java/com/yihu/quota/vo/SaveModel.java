package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by chenweida on 2017/6/1.
 */
public class SaveModel {
    @JestId
    private String id;
    private String saasId;//saasId
    private String quotaCode;//指标code
    private String quotaDate;//统计时间
    private String org;     //机构代码
    private String orgName;//机构名字
    private String province;//省级代码 350000
    private String provinceName;//省名字
    private String city;//城市代码 350200
    private String cityName;//
    private String town;//区代码 350206
    private String townName;//
    private String team;//团队的code
    private String teamName;//
    private String slaveKey1;//从维度  1级维度
    private String slaveKey1Name;
    private String slaveKey2;//从维度  2级维度
    private String slaveKey2Name;
    private String slaveKey3;//从维度  3级维度
    private String slaveKey3Name;
    private String slaveKey4;//从维度  4级维度
    private String slaveKey4Name;
    private Integer result;//统计结果
    private String timeLevel;// 1 日 2 周 3 月 4 年
    private String areaLevel;// 1 省 2 市 3 区县 4 机构 5团队

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
    @CreatedDate
    private Date createTime;//创建时间

    public String getQuotaDate() {
        return quotaDate;
    }

    public void setQuotaDate(String quotaDate) {
        this.quotaDate = quotaDate;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getSlaveKey1() {
        return slaveKey1;
    }

    public void setSlaveKey1(String slaveKey1) {
        this.slaveKey1 = slaveKey1;
    }

    public String getSlaveKey2() {
        return slaveKey2;
    }

    public void setSlaveKey2(String slaveKey2) {
        this.slaveKey2 = slaveKey2;
    }

    public String getSlaveKey3() {
        return slaveKey3;
    }

    public void setSlaveKey3(String slaveKey3) {
        this.slaveKey3 = slaveKey3;
    }

    public String getSlaveKey4() {
        return slaveKey4;
    }

    public void setSlaveKey4(String slaveKey4) {
        this.slaveKey4 = slaveKey4;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getSlaveKey1Name() {
        return slaveKey1Name;
    }

    public void setSlaveKey1Name(String slaveKey1Name) {
        this.slaveKey1Name = slaveKey1Name;
    }

    public String getSlaveKey2Name() {
        return slaveKey2Name;
    }

    public void setSlaveKey2Name(String slaveKey2Name) {
        this.slaveKey2Name = slaveKey2Name;
    }

    public String getSlaveKey3Name() {
        return slaveKey3Name;
    }

    public void setSlaveKey3Name(String slaveKey3Name) {
        this.slaveKey3Name = slaveKey3Name;
    }

    public String getSlaveKey4Name() {
        return slaveKey4Name;
    }

    public void setSlaveKey4Name(String slaveKey4Name) {
        this.slaveKey4Name = slaveKey4Name;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getTimeLevel() {
        return timeLevel;
    }

    public void setTimeLevel(String timeLevel) {
        this.timeLevel = timeLevel;
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSaasId() {
        return saasId;
    }

    public void setSaasId(String saasId) {
        this.saasId = saasId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
