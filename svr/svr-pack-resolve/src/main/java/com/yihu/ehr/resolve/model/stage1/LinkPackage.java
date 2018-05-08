package com.yihu.ehr.resolve.model.stage1;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.core.ResourceCore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 轻量级健康档案。其数据集保存的是机构健康档案中的链接。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Table(ResourceCore.MasterTable)
public class LinkPackage extends StandardPackage {
    public Date expireDate;
    private List<LinkFile> linkFiles = new ArrayList<>();

    public LinkPackage(){
        setProfileType(ProfileType.Link);
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }


    public List<LinkFile> getLinkFiles() {
        return linkFiles;
    }

    public void setLinkFiles(List<LinkFile> linkFiles) {
        this.linkFiles = linkFiles;
    }
}
