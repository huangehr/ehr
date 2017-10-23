package com.yihu.ehr.model.org;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 机构,由 XOrgManager 创建并维护.
 *
 * @author Sand
 * @version 1.0
 * @updated 21-5月-2015 10:51:30
 */
public class MOrganization implements Serializable{
    private String orgCode;         // 机构代码,对医院编码属性需要调研
    private String admin;            // 机构管理员

    private boolean settled;        // 是否已接入,对第三方平台有效.
    private String settledWay;    // 接入方式：直连/平台接入
    private String fullName;        // 全名
    private String orgType;        // 机构类型,如:行政\科研等
    private String pyCode;            // 拼音码
    private String shortName;        // 简称
    private String tel;                // 机构电话
    private Date createDate;        // 创建日期
    private String location;        // 地址
    private Integer activityFlag;
    private String tags;
    private String imgRemotePath;
    private String imgLocalPath;
    private Integer administrativeDivision;
    private String url; //机构网址

    private Long id;
    private String code;        //内部用机构编码
    private String traffic;     //交通路线
    private String photo;       //人口头像
    private String hosTypeId;     //医院类型 字典关联
    private Integer ascriptionType;     //医院归属1.部属医院2.省属医院3.市属医院9：未知
    private String phone;     //联系电话
    private String introduction;     //医院简介
    private String legalPerson;     //法人
    private String levelId;     //医院等级
    private String logoUrl;     //医院LOGO图片
    private Integer sortNo;     //排序ID，按城市排序
    private Integer parentHosId;     //上级医院ID
    private String parentHosName;     //上级医院名称
    private String hosPhoto;     //医院照片
    private Integer zxy;     //中西医
    private String ing;     //经度
    private String lat;     //纬度
    private Date updateTime;//更新时间

    private Integer berth;  //核定床位

    private boolean checked;    //是否选中

    public MOrganization() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getAdmin() {
        return admin;
    }
    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPyCode() {
        return pyCode;
    }
    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSettledWay() {
        return settledWay;
    }

    public void setSettledWay(String settledWay) {
        this.settledWay = settledWay;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    public Integer getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(Integer activityFlag) {
        this.activityFlag = activityFlag;
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

    public Integer getAdministrativeDivision() {
        return administrativeDivision;
    }

    public void setAdministrativeDivision(Integer administrativeDivision) {
        this.administrativeDivision = administrativeDivision;
    }

    public String getParentHosName() {
        return parentHosName;
    }

    public void setParentHosName(String parentHosName) {
        this.parentHosName = parentHosName;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAscriptionType() {
        return ascriptionType;
    }

    public void setAscriptionType(Integer ascriptionType) {
        this.ascriptionType = ascriptionType;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getBerth() {
        return berth;
    }

    public void setBerth(Integer berth) {
        this.berth = berth;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}