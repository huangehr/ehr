package com.yihu.ehr.user.entity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
@Entity
@Table(name = "users")
@Access(value = AccessType.PROPERTY)
public class User {
    private String id;
    private Date createDate;
    private Date lastLoginTime;
    private String email;
    private String loginCode;
    private String realName;
    private String password;
    private Boolean activated;
    private String validateCode;
    private String userType;
    private String telephone;
    private String martialStatus;
    private String gender;
    private String idCardNo;
    private String organization;
    private String source;

    // 未绑定属性
	private String orgPost;
	private String privilege;
	private String role;

    //特殊属性
    private String major;
    private String medicalRole;
    private String techTitle;
    private String adminTitle;
    private String DType;

    private String imgRemotePath;
    private String imgLocalPath;

    public User() {
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "create_date",  nullable = true)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "last_login_time",  nullable = true)
    public Date getLastLoginTime() {
        return lastLoginTime;
    }
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Column(name = "email",  nullable = true)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "login_code",  nullable = false)
    public String getLoginCode() {
        return loginCode;
    }
    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    @Column(name = "real_name",  nullable = false)
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(name = "password",  nullable = false)
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "activated",  nullable = true)
    public Boolean getActivated() {
        return activated;
    }
    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @Column(name = "validate_code",  nullable = true)
    public String getValidateCode() {
        return validateCode;
    }
    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    @Column(name = "user_type",  nullable = true)
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Column(name = "telephone",  nullable = true)
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "martial_status",  nullable = true)
    public String getMartialStatus() {
        return martialStatus;
    }
    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus;
    }

    @Column(name = "gender",  nullable = true)
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Column(name = "id_card_no",  nullable = true)
    public String getIdCardNo() {
        return idCardNo;
    }
    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Column(name = "source",  nullable = true)
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    @Column(name = "organization",  nullable = true)
    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Column(name = "org_post",  nullable = true)
    public String getOrgPost() {
        return orgPost;
    }
    public void setOrgPost(String orgPost) {
        this.orgPost = orgPost;
    }

    @Column(name = "privilege",  nullable = true)
    public String getPrivilege() {
        return privilege;
    }
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    @Column(name = "role",  nullable = true)
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    @Column(name = "major",  nullable = true)
    public String getMajor() {
        return major;
    }
    public void setMajor(String major) {
        this.major = major;
    }

    @Column(name = "medical_role",  nullable = true)
    public String getMedicalRole() {
        return medicalRole;
    }
    public void setMedicalRole(String medicalRole) {
        this.medicalRole = medicalRole;
    }

    @Column(name = "tech_title",  nullable = true)
    public String getTechTitle() {
        return techTitle;
    }
    public void setTechTitle(String techTitle) {
        this.techTitle = techTitle;
    }

    @Column(name = "admin_title",  nullable = true)
    public String getAdminTitle() {
        return adminTitle;
    }
    public void setAdminTitle(String adminTitle) {
        this.adminTitle = adminTitle;
    }

    @Column(name = "DType",  nullable = true)
    public String getDType() {
        return DType;
    }
    public void setDType(String DType) {
        this.DType = DType;
    }

    @Column(name = "img_remote_path",  nullable = true)
    public String getImgRemotePath() {
        return imgRemotePath;
    }
    public void setImgRemotePath(String imgRemotePath) {
        this.imgRemotePath = imgRemotePath;
    }

    @Column(name = "img_local_path",  nullable = true)
    public String getImgLocalPath() {
        return imgLocalPath;
    }
    public void setImgLocalPath(String imgLocalPath) {
        this.imgLocalPath = imgLocalPath;
    }
}