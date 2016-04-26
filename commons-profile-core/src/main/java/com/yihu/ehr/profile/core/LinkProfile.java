package com.yihu.ehr.profile.core;

import com.yihu.ehr.constants.ProfileType;

import java.util.Date;

/**
 * 轻量级健康档案。其数据集保存的是机构健康档案中的链接。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class LinkProfile extends StructedProfile {
    public Date expireDate;

    public LinkProfile(){
        setProfileType(ProfileType.Lightweight);
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
