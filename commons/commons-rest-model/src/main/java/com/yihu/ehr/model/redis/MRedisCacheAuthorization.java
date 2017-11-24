package com.yihu.ehr.model.redis;


import java.io.Serializable;
import java.util.Date;

/**
 * 缓存授权
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
public class MRedisCacheAuthorization implements Serializable{

    public Integer id; // 主键
    private String categoryCode; // 缓存分类编码
    private String appId; // 应用ID
    private String authorizedCode; // 授权码
    private String remark; // 备注
    private Date createDate; // 创建时间
    private String creator; // 创建者
    private Date modifyDate; // 修改时间
    private String modifier; // 修改者

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAuthorizedCode() {
        return authorizedCode;
    }

    public void setAuthorizedCode(String authorizedCode) {
        this.authorizedCode = authorizedCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
