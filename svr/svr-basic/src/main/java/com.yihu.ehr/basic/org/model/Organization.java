package com.yihu.ehr.basic.org.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer provinceId;

    @Column(name = "province_name",nullable = true)
    private String provinceName;

    @Column(name = "city_id",nullable = true)
    private Integer cityId;

    @Column(name = "city_name",nullable = true)
    private String cityName;

    @Column(name = "area_id",nullable = true)
    private Integer areaId;

    @Column(name = "area_name",nullable = true)
    private String areaName;

    @Column(name = "photo",nullable = true)
    private String photo;

    @Column(name = "hos_type_id",nullable = true)
    private String hosTypeId;

    @Column(name = "url",nullable = true)
    private String url;

    @Column(name = "ascription_type",nullable = true)
    private Integer ascriptionType;

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
    private Integer sortNo;

    @Column(name = "parent_hos_id",nullable = true)
    private Integer parentHosId;

    @Column(name = "hos_photo",nullable = true)
    private String hosPhoto;

    @Column(name = "zxy",nullable = true)
    private Integer zxy;

    @Column(name = "ing",nullable = true)
    private String ing;

    @Column(name = "lat",nullable = true)
    private String lat;

    @Column(name = "update_time",nullable = true)
    private Date updateTime;

    @Column(name = "berth",nullable = true)
    private Integer berth;  //核定床位

    @Column(name = "hos_hierarchy", nullable = true)
    private String hosHierarchy;//医院等次 ：1：特等、2：甲等、3：乙等、4：丙等、9：未评

    @Column(name = "hos_economic", nullable = true)
    private String hosEconomic;//经济类型代码 与 资源字典对应

    @Column(name = "classification", nullable = true)
    private String classification;//卫生机构分类，值参考系统字典卫生机构分类

    @Column(name = "big_classification", nullable = true)
    private String bigClassification;//卫生机构大分类，值参考系统字典卫生机构大分类

    @Column(name = "nature", nullable = true)
    private String nature;//机构性质1，1公立、2民营

    @Column(name = "branch_type", nullable = true)
    private String branchType;//机构性质Ⅱ，1总院、2分院

    @Column(name = "display_status", nullable = true)
    private String displayStatus;//与总部同步数据补充字段是否开放显示：0：不显示 1:显示

    @Column(name = "jkzl_org_id", nullable = true)
    private String jkzlOrgId;//总部机构id-同步数据使用
    @Column(name = "host_unit", nullable = true)
    private String hostUnit;     //	主办单位

    @Column(name = "hos_manage_type", nullable = true)
    private String hosManageType;     //	机构分类管理代码

    @Column(name = "basic_unit_flag", nullable = true)
    private String basicUnitFlag;     //	是否基层单位

    @Column(name = "org_changes", nullable = true)
    private String orgChanges;         //	机构变动情况

    @Column(name = "hos_type_name", nullable = true)
    private String hosTypeName;     // 机构类别代码名称

    @Column(name = "street_id", nullable = true)
    private String streetId;     // 街道/乡镇代码

    @Column(name = "discharge_patient_flag", nullable = true)
    private String dischargePatientFlag;    //	是否填报出院病人表

    @Column(name = "reporting_clinic_flag", nullable = true)
    private String reportingClinicFlag;    //	是否代报诊所

    @Column(name = "reporting_village_clinic_flag", nullable = true)
    private String reportingVillageClinicFlag;    //	是否代报村卫生室

    @Column(name = "reporting_org", nullable = true)
    private String reportingOrg;        // 诊所、村卫生室所属代报机构

    @Column(name = "founding_time", nullable = true)
    private String foundingTime;    //	单位开业/成立时间

    @Column(name = "registered_capital", nullable = true)
    private String registeredCapital;    //	注册资金(万元)

    @Column(name = "branch_org_flag", nullable = true)
    private String branchOrgFlag;        // 是否分支机构

    @Column(name = "postalcode", nullable = true)
    private String postalcode;    //	邮政编码

    @Column(name = "email", nullable = true)
    private String email;        // 电子邮箱

    @Column(name = "domain_name", nullable = true)
    private String domainName;        // 单位网站域名

    @Column(name = "registration_number", nullable = true)
    private String registrationNumber;    //	批准文号/注册号

    @Column(name = "registration_ratification_agency", nullable = true)
    private String registrationRatificationAgency;    //	登记批准机构

    @Column(name = "certificate_date", nullable = true)
    private String certificateDate;    //	办证日期

    @Column(name = "operator", nullable = true)
    private String operator;    //	经办人

    @Column(name = "entry_staff", nullable = true)
    private String entryStaff;     //	录入人

    @Column(name = "create_time", nullable = true)
    private String createTime;     //	新增机构创建时间

    @Column(name = "cancel_time", nullable = true)
    private String cancelTime;    //	作废日期

    @Column(name = "term_validity_start", nullable = true)
    private String termValidityStart;    //	有效期起

    @Column(name = "term_validity_end", nullable = true)
    private String termValidityEnd;    //	有效期止




    public Organization() {
        //tags = new HashSet<>();
    }


    public Integer getAscriptionType() {
        return ascriptionType;
    }

    public void setAscriptionType(Integer ascriptionType) {
        this.ascriptionType = ascriptionType;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    public String getHosHierarchy() {
        return hosHierarchy;
    }

    public void setHosHierarchy(String hosHierarchy) {
        this.hosHierarchy = hosHierarchy;
    }

    public String getHosEconomic() {
        return hosEconomic;
    }

    public void setHosEconomic(String hosEconomic) {
        this.hosEconomic = hosEconomic;
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

    public String getHostUnit() {
        return hostUnit;
    }

    public void setHostUnit(String hostUnit) {
        this.hostUnit = hostUnit;
    }

    public String getHosManageType() {
        return hosManageType;
    }

    public void setHosManageType(String hosManageType) {
        this.hosManageType = hosManageType;
    }

    public String getBasicUnitFlag() {
        return basicUnitFlag;
    }

    public void setBasicUnitFlag(String basicUnitFlag) {
        this.basicUnitFlag = basicUnitFlag;
    }

    public String getOrgChanges() {
        return orgChanges;
    }

    public void setOrgChanges(String orgChanges) {
        this.orgChanges = orgChanges;
    }

    public String getHosTypeName() {
        return hosTypeName;
    }

    public void setHosTypeName(String hosTypeName) {
        this.hosTypeName = hosTypeName;
    }

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId;
    }

    public String getDischargePatientFlag() {
        return dischargePatientFlag;
    }

    public void setDischargePatientFlag(String dischargePatientFlag) {
        this.dischargePatientFlag = dischargePatientFlag;
    }

    public String getReportingClinicFlag() {
        return reportingClinicFlag;
    }

    public void setReportingClinicFlag(String reportingClinicFlag) {
        this.reportingClinicFlag = reportingClinicFlag;
    }

    public String getReportingVillageClinicFlag() {
        return reportingVillageClinicFlag;
    }

    public void setReportingVillageClinicFlag(String reportingVillageClinicFlag) {
        this.reportingVillageClinicFlag = reportingVillageClinicFlag;
    }

    public String getReportingOrg() {
        return reportingOrg;
    }

    public void setReportingOrg(String reportingOrg) {
        this.reportingOrg = reportingOrg;
    }

    public String getFoundingTime() {
        return foundingTime;
    }

    public void setFoundingTime(String foundingTime) {
        this.foundingTime = foundingTime;
    }

    public String getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getBranchOrgFlag() {
        return branchOrgFlag;
    }

    public void setBranchOrgFlag(String branchOrgFlag) {
        this.branchOrgFlag = branchOrgFlag;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationRatificationAgency() {
        return registrationRatificationAgency;
    }

    public void setRegistrationRatificationAgency(String registrationRatificationAgency) {
        this.registrationRatificationAgency = registrationRatificationAgency;
    }

    public String getCertificateDate() {
        return certificateDate;
    }

    public void setCertificateDate(String certificateDate) {
        this.certificateDate = certificateDate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getEntryStaff() {
        return entryStaff;
    }

    public void setEntryStaff(String entryStaff) {
        this.entryStaff = entryStaff;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getTermValidityStart() {
        return termValidityStart;
    }

    public void setTermValidityStart(String termValidityStart) {
        this.termValidityStart = termValidityStart;
    }

    public String getTermValidityEnd() {
        return termValidityEnd;
    }

    public void setTermValidityEnd(String termValidityEnd) {
        this.termValidityEnd = termValidityEnd;
    }
}