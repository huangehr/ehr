package com.yihu.ehr.model.org;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String hosHierarchy;//医院等次 ：1：特等、2：甲等、3：乙等、4：丙等、9：未评

    private String hosEconomic;//经济类型代码 与 资源字典对应

    private String classification;//卫生机构分类，值参考系统字典卫生机构分类

    private String bigClassification;//卫生机构大分类，值参考系统字典卫生机构大分类

    private String nature;//机构性质1，1公立、2民营

    private String branchType;//机构性质Ⅱ，1总院、2分院

    private String displayStatus;//与总部同步数据补充字段是否开放显示：0：不显示 1:显示

    private String jkzlOrgId;//总部机构id-同步数据使用

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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    public String getHosEconomic() {
        return hosEconomic;
    }

    public void setHosEconomic(String hosEconomic) {
        this.hosEconomic = hosEconomic;
    }

    public String getHosHierarchy() {
        return hosHierarchy;
    }

    public void setHosHierarchy(String hosHierarchy) {
        this.hosHierarchy = hosHierarchy;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getBigClassification() {
        return bigClassification;
    }

    public void setBigClassification(String bigClassification) {
        this.bigClassification = bigClassification;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getJkzlOrgId() {
        return jkzlOrgId;
    }

    public void setJkzlOrgId(String jkzlOrgId) {
        this.jkzlOrgId = jkzlOrgId;
    }
}