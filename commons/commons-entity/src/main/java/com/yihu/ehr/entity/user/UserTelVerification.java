package com.yihu.ehr.entity.user;

import com.yihu.ehr.entity.BaseIdentityEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
public class UserTelVerification  implements Serializable{
    /** ID */
    private Integer id;
    /** 用于登录的手机号码 */
    private String telNo;
    /** 手机短信验证码 */
    private String verificationCode;
    /** 有效时间，当前时间+10分钟 */
    private Date effectivePeriod;
    /** 用于验证码的应用 */
    private String appId;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "identity")
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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