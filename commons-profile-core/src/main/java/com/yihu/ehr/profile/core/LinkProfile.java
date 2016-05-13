package com.yihu.ehr.profile.core;

import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.util.ProfileUtil;

import java.util.Date;

/**
 * 轻量级健康档案。其数据集保存的是机构健康档案中的链接。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Table(ProfileUtil.Table)
public class LinkProfile extends StdProfile {
    public Date expireDate;

    public LinkProfile(){
        setProfileType(ProfileType.Link);
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
