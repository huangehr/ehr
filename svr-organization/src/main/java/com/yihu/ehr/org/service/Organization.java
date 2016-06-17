package com.yihu.ehr.org.service;

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
    private boolean settled;        // 是否已接入,对第三方平台有效.

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
    private int activityFlag;

    @Column(name = "tags",  nullable = true)
    private String tags;


    @Column(name = "img_remote_path",nullable = true)
    private String imgRemotePath;

    @Column(name = "img_local_path",nullable = true)
    private String imgLocalPath;

    @Column(name = "administrative_division",nullable = true)
    private int administrativeDivision;

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


    public boolean isSettled() {
        return settled;
    }
    public void setSettled(boolean settled) {
        this.settled = settled;
    }


    public int getActivityFlag() {
        return activityFlag;
    }
    public void setActivityFlag(int activityFlag) {
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


    public int getAdministrativeDivision() {
        return administrativeDivision;
    }
    public void setAdministrativeDivision(int administrativeDivision) {
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


}