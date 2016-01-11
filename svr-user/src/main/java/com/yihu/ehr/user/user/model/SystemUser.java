package com.yihu.ehr.user.user.model;

import com.yihu.ha.dict.model.common.Gender;
import com.yihu.ha.dict.model.common.MartialStatus;
import com.yihu.ha.dict.model.common.UserRole;
import com.yihu.ha.dict.model.common.UserType;
import com.yihu.ha.organization.model.XOrgPost;
import com.yihu.ha.organization.model.XOrganization;

import java.util.Date;

/**
 * Created by zqb on 2015/10/15.
 */
public class SystemUser implements XUser{

    public String id;
    public SystemUser(){
        id="system";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Date getCreateDate() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getTelephone() {
        return null;
    }

    @Override
    public MartialStatus getMartialStatus() {
        return null;
    }

    @Override
    public Gender getGender() {
        return null;
    }

    @Override
    public String getIdCardNo() {
        return null;
    }

    @Override
    public Date getLastLoginTime() {
        return null;
    }

    @Override
    public String getLoginCode() {
        return null;
    }

    @Override
    public String getRealName() {
        return null;
    }

    @Override
    public XOrganization getOrganization() {
        return null;
    }

    @Override
    public XOrgPost getOrgPost() {
        return null;
    }

    @Override
    public XPrivilege getPrivilege() {
        return null;
    }

    @Override
    public UserRole getRole() {
        return null;
    }

    @Override
    public UserType getUserType() {
        return null;
    }

    @Override
    public String getValidateCode() {
        return null;
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public boolean isPasswordRight(String pwd) {
        return false;
    }

    @Override
    public String hashPassword(String pwd) {
        return null;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public void setTelephone(String telephone) {

    }

    @Override
    public void setMartialStatus(MartialStatus martialStatus) {

    }

    @Override
    public void setGender(Gender gender) {

    }

    @Override
    public void setIdCardNo(String idCardNo) {

    }

    @Override
    public void setRealName(String name) {

    }

    @Override
    public void setOrganization(XOrganization org) {

    }

    @Override
    public void setOrganization(String orgCode) {

    }

    @Override
    public void setOrgPost(XOrgPost post) {

    }

    @Override
    public void setPrivilege(XPrivilege privilege) {

    }

    @Override
    public void setRole(UserRole role) {

    }

    @Override
    public void setUserType(UserType type) {

    }

    @Override
    public void setValidateCode(String validateCode) {

    }

    @Override
    public void updatePassword(String oriPwd, String newPwd) {

    }

    @Override
    public void resetPassword() {

    }

    @Override
    public void setLastLoginTime(Date date){

    }

    @Override
    public Boolean getActivated() {
        return null;
    }

    @Override
    public void setActivated(Boolean activated) {

    }

}
