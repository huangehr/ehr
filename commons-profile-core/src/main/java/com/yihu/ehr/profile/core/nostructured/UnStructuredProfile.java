package com.yihu.ehr.profile.core.nostructured;

import com.yihu.ehr.profile.core.commons.Profile;

import java.util.List;

/**
 * 健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class UnStructuredProfile extends Profile{

    //非结构化data数组内容
    private List<UnStructuredDocument> unStructuredDocument;

    public List<UnStructuredDocument> getUnStructuredDocument() {
        return unStructuredDocument;
    }

    public void setUnStructuredDocument(List<UnStructuredDocument> unStructuredDocument) {
        this.unStructuredDocument = unStructuredDocument;
    }

}
