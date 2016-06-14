package com.yihu.ehr.agModel.org;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yww on 2016/2/22.
 */
public class OrgDetailModel{
    private String orgCode;
    private String fullName;
    private String shortName;
    private String pyCode;
    private String orgType;
    private String orgTypeName;
    private boolean settled;
    private String settledWay;
    private String settledWayName;
    private int activityFlag;
    private String activityFlgName;
    private String createDate;
    private String location;
    private String province;
    private String city;
    private String district;
    private String town;
    private String street;
    private String extra;
    private String admin;
    private String tel;
    private String tags;
    private String imgRemotePath;
    private String imgLocalPath;
    private int administrativeDivision;
    //公钥信息
    String publicKey;
    String validTime;
    String startTime;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
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

    public boolean getSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
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

    public int getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(int activityFlag) {
        this.activityFlag = activityFlag;
    }

    public String getActivityFlgName() {
        return activityFlgName;
    }

    public void setActivityFlgName(String activityFlgName) {
        this.activityFlgName = activityFlgName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<String> getTags() {
        List<String> list = new ArrayList<>();
        if(StringUtils.isEmpty(tags)){
        }else {
            String[] arr = tags.split(";|；");
            list = Arrays.asList(arr);
        }
        return list;
    }
    public void setTags(List<String> tags) {
        if(tags.size()>0){
            this.tags = StringUtils.join(tags.toArray(),";");
        }else {
            this.tags = "";
        }
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
}