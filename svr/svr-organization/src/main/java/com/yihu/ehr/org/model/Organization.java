package com.yihu.ehr.org.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

/**
 * 机构,由 XOrgManager 创建并维护.
 *
 * @author Sand
 * @version 1.0
 * @updated 21-5月-2015 10:51:30
 */
@Entity
@Table(name = "organizations")
@Access(value = AccessType.FIELD)
public class Organization  {


    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "org_code", unique = true, nullable = false)
    private String orgCode;         // 机构代码,对医院编码属性需要调研

    @Column(name = "admin",  nullable = true)
    private String admin;            // 机构管理员

    @Column(name = "settled",  nullable = true)
    private Boolean settled;        // 是否已接入,对第三方平台有效.

    @Column(name = "settled_way",  nullable = true)
    private String settledWay;    // 接入方式：直连/平台接入

    @Column(name = "full_name",  nullable = true)
    private String fullName;        // 全名

    @Column(name = "org_type",  nullable = true)
    private String orgType;        // 机构类型,如:行政\科研等

    @Column(name = "py_code",  nullable = true)
    private String pyCode;            // 拼音码

    @Column(name = "short_name",  nullable = true)
    private String shortName;        // 简称
    //private Set<String> tags;        // 标签

    @Column(name = "tel",  nullable = true)
    private String tel;                // 机构电话

    @Column(name = "create_date",  nullable = true)
    private Date createDate;        // 创建日期

    @Column(name = "location",  nullable = true)
    private String location;        // 地址

    @Column(name = "activity_flag", unique = true, nullable = true)
    private Integer activityFlag;

    @Column(name = "tags",  nullable = true)
    private String tags;


    @Column(name = "img_remote_path",nullable = true)
    private String imgRemotePath;

    @Column(name = "img_local_path",nullable = true)
    private String imgLocalPath;

    @Column(name = "administrative_division",nullable = true)
    private Integer administrativeDivision;

    @Column(name = "id",nullable = true)
    private Long id;

    @Column(name = "code",nullable = true)
    private String code;

    @Column(name = "traffic",nullable = true)
    private String traffic;

    @Column(name = "province_id",nullable = true)
    private String provinceId;

    @Column(name = "province_name",nullable = true)
    private String provinceName;

    @Column(name = "city_id",nullable = true)
    private String cityId;

    @Column(name = "city_name",nullable = true)
    private String cityName;

    @Column(name = "area_id",nullable = true)
    private String areaId;

    @Column(name = "area_name",nullable = true)
    private String areaName;

    @Column(name = "photo",nullable = true)
    private String photo;

    @Column(name = "hos_type_id",nullable = true)
    private String hosTypeId;

    @Column(name = "url",nullable = true)
    private String url;

    @Column(name = "ascription_type",nullable = true)
    private String ascription_type;

    @Column(name = "phone",nullable = true)
    private String phone;

    @Column(name = "introduction",nullable = true)
    private String introduction;

    @Column(name = "legal_person",nullable = true)
    private String legalPerson;

    @Column(name = "level_id",nullable = true)
    private String levelId;

    @Column(name = "logo_url",nullable = true)
    private String logoUrl;

    @Column(name = "sort_no",nullable = true)
    private String sortNo;

    @Column(name = "parent_hos_id",nullable = true)
    private String parentHosId;

    @Column(name = "hos_photo",nullable = true)
    private String hosPhoto;

    @Column(name = "zxy",nullable = true)
    private String zxy;

    @Column(name = "ing",nullable = true)
    private String ing;

    @Column(name = "lat",nullable = true)
    private String lat;

    @Column(name = "update_time",nullable = true)
    private String updateTime;

    public Organization() {
        //tags = new HashSet<>();
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


    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }


    public String getPyCode() {
        return pyCode;
    }
    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }


    public String getSettledWay() {
        return settledWay;
    }

    public void setSettledWay(String settledWay) {
        this.settledWay = settledWay;
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


    public String getOrgType() {
        return orgType;
    }
    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }


    public Boolean isSettled() {
        return settled;
    }
    public void setSettled(Boolean settled) {
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


    public List<String> getTags() {
        List<String> list = new ArrayList<>();
        if(org.springframework.util.StringUtils.isEmpty(tags)){
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

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAscription_type() {
        return ascription_type;
    }

    public void setAscription_type(String ascription_type) {
        this.ascription_type = ascription_type;
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

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getParentHosId() {
        return parentHosId;
    }

    public void setParentHosId(String parentHosId) {
        this.parentHosId = parentHosId;
    }

    public String getHosPhoto() {
        return hosPhoto;
    }

    public void setHosPhoto(String hosPhoto) {
        this.hosPhoto = hosPhoto;
    }

    public String getZxy() {
        return zxy;
    }

    public void setZxy(String zxy) {
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