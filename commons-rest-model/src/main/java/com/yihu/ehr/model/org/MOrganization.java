package com.yihu.ehr.model.org;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.dict.MConventionalDict;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
    private String settled;        // 是否已接入,对第三方平台有效.
    private MConventionalDict settledWay;    // 接入方式：直连/平台接入
    private String fullName;        // 全名
    private MConventionalDict orgType;        // 机构类型,如:行政\科研等
    private String pyCode;            // 拼音码
    private String shortName;        // 简称
    private Set<String> tags;        // 标签
    private String tel;                // 机构电话
    private String createDate;        // 创建日期
    private MAddress location;        // 地址
    private String activityFlag;

    public MOrganization() {
        tags = new HashSet();
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

    public String getTags() {
        return tags.toString();
    }
    public void setTags(String tags) {
        if(tags == null) return;
        String[] tagToken = tags.split(",");
        for (String token: tagToken){
            token = token.trim();
            if(token.length() == 0) continue;
            this.tags.add(token);
        }
    }
    String getTagsStr(){
        return String.join(",", tags);
    }
    public void addTag(String tag) {
        if(tag == null || tag.length() == 0) return;
        if (tags.contains(tag)) return;
        tags.add(tag);
    }
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }



    public String getPyCode() {
        return pyCode;
    }
    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }


    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
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

    public MConventionalDict getSettledWay() {
        return settledWay;
    }

    public void setSettledWay(MConventionalDict settledWay) {
        this.settledWay = settledWay;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public MConventionalDict getOrgType() {
        return orgType;
    }

    public void setOrgType(MConventionalDict orgType) {
        this.orgType = orgType;
    }

    public MAddress getLocation() {
        return location;
    }

    public void setLocation(MAddress location) {
        this.location = location;
    }

    public String getSettled() {
        return settled;
    }
    public void setSettled(String settled) {
        this.settled = settled;
    }

    public String getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(String activityFlag) {
        this.activityFlag = activityFlag;
    }

}