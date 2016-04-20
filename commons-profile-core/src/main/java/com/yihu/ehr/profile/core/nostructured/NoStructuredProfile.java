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
public class NoStructuredProfile extends Profile{

    //非结构化data数组内容
    private List<NoStructuredDocument> noStructuredDocumentList;

    public List<NoStructuredDocument> getNoStructuredDocumentList() {
        return noStructuredDocumentList;
    }

    public void setNoStructuredDocumentList(List<NoStructuredDocument> noStructuredDocumentList) {
        this.noStructuredDocumentList = noStructuredDocumentList;
    }

}
