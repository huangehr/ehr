package com.yihu.ehr.org.service;

import org.apache.commons.lang.StringUtils;
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
    private String admin;            // 机构管理员
    private boolean settled;        // 是否已接入,对第三方平台有效.
    private String settledWay;    // 接入方式：直连/平台接入
    private String fullName;        // 全名
    private String orgType;        // 机构类型,如:行政\科研等
    private String pyCode;            // 拼音码
    private String shortName;        // 简称
    //private Set<String> tags;        // 标签
    private String tel;                // 机构电话
    private Date createDate;        // 创建日期
    private String location;        // 地址
    private int activityFlag;
    private String tags;
    public Organization() {
        //tags = new HashSet<>();
    }


    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "admin",  nullable = true)
    public String getAdmin() {
        return admin;
    }
    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Column(name = "create_date",  nullable = true)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "full_name",  nullable = true)
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "location",  nullable = true)
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name = "py_code",  nullable = true)
    public String getPyCode() {
        return pyCode;
    }
    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    @Column(name = "settled_way",  nullable = true)
    public String getSettledWay() {
        return settledWay;
    }

    public void setSettledWay(String settledWay) {
        this.settledWay = settledWay;
    }


    @Column(name = "short_name",  nullable = true)
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }


    @Column(name = "tel",  nullable = true)
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    @Column(name = "org_type",  nullable = true)
    public String getOrgType() {
        return orgType;
    }
    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    @Column(name = "settled",  nullable = true)
    public boolean isSettled() {
        return settled;
    }
    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    @Column(name = "activity_flag", unique = true, nullable = true)
    public int getActivityFlag() {
        return activityFlag;
    }
    public void setActivityFlag(int activityFlag) {
        this.activityFlag = activityFlag;
    }

    @Column(name = "tags",  nullable = true)
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