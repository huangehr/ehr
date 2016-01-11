package com.yihu.ehr.user.user.model;

import com.yihu.ha.constrant.BizObject;
import com.yihu.ha.constrant.EnvironmentOptions;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.dict.model.common.Gender;
import com.yihu.ha.dict.model.common.MartialStatus;
import com.yihu.ha.dict.model.common.UserRole;
import com.yihu.ha.dict.model.common.UserType;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.organization.model.XOrgPost;
import com.yihu.ha.organization.model.XOrganization;
import com.yihu.ha.util.ObjectId;
import com.yihu.ha.util.XEnvironmentOption;
import com.yihu.ha.util.encode.HashUtil;
import com.yihu.ha.util.operator.StringUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽象用户类. 只保存特定属性, 并实现一些通用 XUser 接口方法. 虽然名为抽象类, 但为了能够使用Hibernate, 将abstract关键字去掉了.
 *
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
public abstract class AbstractUser implements XUser, Serializable {

    protected ObjectId id;

    protected Date createDate;

    protected Date lastLoginTime;

    protected String email;

    protected String loginCode;

    protected String realName;

    protected String password;

    protected Boolean activated;

    protected String validateCode;

    protected UserType userType;

    protected String telephone;

    protected MartialStatus martialStatus;

    protected Gender gender;

    protected String idCardNo;

    protected XOrganization organization;

    // 未绑定属性
	protected XOrgPost orgPost;
	protected XPrivilege privilege;
	protected UserRole role;

    public AbstractUser() {
        XEnvironmentOption environmentOption = ServiceFactory.getService(Services.EnvironmentOption);
        id = new ObjectId(Short.parseShort(environmentOption.getOption(EnvironmentOptions.AdminRegion)), BizObject.User);
    }

    public String hashPassword(String pwd) {

        return HashUtil.hashStr(pwd);
    }

    public String getId() {
        return id.toString();
    }

    private void setId(String id) {
        this.id = new ObjectId(id);
    }

    public Date getCreateDate() {
        return createDate;
    }

    void setCreateDate(Date date) {
        this.createDate = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date date) {
        this.lastLoginTime = date;
    }

    public String getLoginCode() {
        return loginCode;
    }

    void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String name) {
        realName = name;
    }

    /**
     * 判断用户密码是否正确
     *
     * @param pwd
     * @return
     */
    public boolean isPasswordRight(String pwd) {
        return hashPassword(pwd).equals(password);
    }

    public void updatePassword(String oriPwd, String newPwd) {
        if(!isPasswordRight(oriPwd)) throw new RuntimeException("Original password incorrect.");

        password = hashPassword(newPwd);
    }

    public void resetPassword() {
        XEnvironmentOption environmentConfig = ServiceFactory.getService(Services.EnvironmentOption);
        String pwd = environmentConfig.getOption(EnvironmentOptions.DefaultPassword);
        password = hashPassword(pwd);
    }

    public XOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(XOrganization org) {
        this.organization = org;
    }

    public void setOrganization(String orgCode) {
        if (!StringUtil.isEmpty(orgCode)) {
            XOrgManager orgManager = ServiceFactory.getService(Services.OrgManager);
            this.organization = orgManager.getOrg(orgCode);
        }
    }

    public XOrgPost getOrgPost() {
        return null;//orgPost;
    }

    public void setOrgPost(XOrgPost post) {
        //this.orgPost = post;
    }

    public XPrivilege getPrivilege() {
        return null;//privilege;
    }

    public void setPrivilege(XPrivilege privilege) {
        //this.privilege = privilege;
    }

    public UserRole getRole() {
        return null;//role;
    }

    public void setRole(UserRole role) {
        //this.role = role;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType type) {
        this.userType = type;
    }

    public String getTelephone(){
        return telephone;
    }

    public void setTelephone(String telephone){
        this.telephone = telephone;
    }

    public MartialStatus getMartialStatus(){
        return this.martialStatus;
    }

    public void setMartialStatus(MartialStatus martialStatus){
        this.martialStatus = martialStatus;
    }

    public Gender getGender(){
        return this.gender;
    }

    public void setGender(Gender gender){
        this.gender = gender;
    }

    public String getIdCardNo(){
        return this.idCardNo;
    }

    public void setIdCardNo(String idCardNo){
        this.idCardNo = idCardNo;
    }

    public String getValidateCode() {
        return this.validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public boolean isActivated() {
        return activated;
    }

    public Boolean getActivated() {
        return activated;
    }

    @Override
    public void setActivated(Boolean activated) {
        this.activated = activated;
    }
}