package com.yihu.ehr.agModel.org;

import java.util.Date;

/**
 * 用于Grid显示
 * Created by yww on 2016/2/22.
 */

public class OrgModel {
    private String orgCode;
    private String orgType;
    private String orgTypeName;
    private String pyCode;
    private String fullName;
    private String admin;
    private String tel;
    private String location;
    private String locationStrName;
    private int activityFlag;
    private String activityFlagName;
    private String settledWay;
    private String settledWayName;
    private String createDate;
    private String imgRemotePath;
    private String imgLocalPath;
    private int administrativeDivision;

    private Long id;
    private String code;        //内部用机构编码
    private String traffic;     //交通路线
    private String photo;       //人口头像
    private String hosTypeId;     //医院类型 字典关联
    private String hosTypeName;
    private Integer ascriptionType;     //医院归属1.部属医院2.省属医院3.市属医院9：未知
    private String ascriptionTypeName;
    private String phone;     //联系电话
    private String introduction;     //医院简介
    private String legalPerson;     //法人
    private String levelId;     //医院等级
    private String logoUrl;     //医院LOGO图片
    private Integer sortNo;     //排序ID，按城市排序
    private Integer parentHosId;     //上级医院ID
    private Integer zxy;     //中西医
    private String hosPhoto;     //医院照片
    private String ing;     //经度
    private String lat;     //纬度
    private String updateTime;//更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgTypeName() {
        return orgTypeName;
    }

    public void setOrgTypeName(String orgTypeName) {
        this.orgTypeName = orgTypeName;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationStrName() {
        return locationStrName;
    }

    public void setLocationStrName(String locationStrName) {
        this.locationStrName = locationStrName;
    }

    public int getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(int activityFlag) {
        this.activityFlag = activityFlag;
    }

    public String getActivityFlagName() {
        return activityFlagName;
    }

    public void setActivityFlagName(String activityFlagName) {
        this.activityFlagName = activityFlagName;
    }

    public String getSettledWay() {
        return settledWay;
    }

    public void setSettledWay(String settledWay) {
        this.settledWay = settledWay;
    }

    public String getSettledWayName() {
        return settledWayName;
    }

    public void setSettledWayName(String settledWayName) {
        this.settledWayName = settledWayName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getImgRemotePath() {
        return imgRemotePath;
    }

    public void setImgRemotePath(String imgRemotePath) {
        this.imgRemotePath = imgRemotePath;
    }

    public String getImgLocalPath() {
        return imgLocalPath;
    }

    public void setImgLocalPath(String imgLocalPath) {
        this.imgLocalPath = imgLocalPath;
    }

    public int getAdministrativeDivision() {
        return administrativeDivision;
    }

    public void setAdministrativeDivision(int administrativeDivision) {
        this.administrativeDivision = administrativeDivision;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getHosTypeId() {
        return hosTypeId;
    }

    public void setHosTypeId(String hosTypeId) {
        this.hosTypeId = hosTypeId;
    }

    public String getHosTypeName() {
        return hosTypeName;
    }

    public void setHosTypeName(String hosTypeName) {
        this.hosTypeName = hosTypeName;
    }

    public Integer getAscriptionType() {
        return ascriptionType;
    }

    public void setAscriptionType(Integer ascriptionType) {
        this.ascriptionType = ascriptionType;
    }

    public String getAscriptionTypeName() {
        return ascriptionTypeName;
    }

    public void setAscriptionTypeName(String ascriptionTypeName) {
        this.ascriptionTypeName = ascriptionTypeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getParentHosId() {
        return parentHosId;
    }

    public void setParentHosId(Integer parentHosId) {
        this.parentHosId = parentHosId;
    }

    public String getHosPhoto() {
        return hosPhoto;
    }

    public void setHosPhoto(String hosPhoto) {
        this.hosPhoto = hosPhoto;
    }

    public Integer getZxy() {
        return zxy;
    }

    public void setZxy(Integer zxy) {
        this.zxy = zxy;
    }

    public String getIng() {
        return ing;
    }

    public void setIng(String ing) {
        this.ing = ing;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}