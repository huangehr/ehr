package com.yihu.ehr.entity.user;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 手机验证码表，用于通过短信验证码进行密码的更新
 *
 * @author cws
 * @version 1.0
 * @created 22-2月-2018
 */
@Entity
@Table(name = "user_tel_verification")
public class UserTelVerification extends BaseIdentityEntity{

    private String telNo;
    private String verificationCode;
    private Date effectivePeriod;
    private String appId;

    @Column(name = "tel_no",  nullable = true)
    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    @Column(name = "verification_code",  nullable = true)
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode =  verificationCode;
    }

    @Column(name = "effective_period",  nullable = true)
    public Date getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(Date effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    @Column(name = "app_id",  nullable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}