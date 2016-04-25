package com.yihu.ehr.profile.core.nostructured;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.core.commons.Profile;

import java.util.List;

/**
 * 非结构化健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class NoStructuredProfile extends Profile{

    private ProfileType profileType;                         // 档案类型

    //非结构化data数组内容
    private List<NoStructuredDocument> noStructuredDocumentList;

    public ProfileType getProfileType() {
        return ProfileType.NoStructured;
    }
    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public List<NoStructuredDocument> getNoStructuredDocumentList() {
        return noStructuredDocumentList;
    }

    public void setNoStructuredDocumentList(List<NoStructuredDocument> noStructuredDocumentList) {
        this.noStructuredDocumentList = noStructuredDocumentList;
    }
}
