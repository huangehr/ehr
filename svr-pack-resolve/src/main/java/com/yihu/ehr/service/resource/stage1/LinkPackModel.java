package com.yihu.ehr.service.resource.stage1;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.service.util.ResourceStorageUtil;

import java.util.Date;

/**
 * 轻量级健康档案。其数据集保存的是机构健康档案中的链接。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Table(ResourceStorageUtil.MasterTable)
public class LinkPackModel extends StdPackModel {
    public Date expireDate;

    public LinkPackModel(){
        setProfileType(ProfileType.Link);
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
