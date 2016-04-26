package com.yihu.ehr.profile.core.nostructured;

import com.yihu.ehr.profile.core.StructedProfile;

import java.util.List;
import java.util.Map;

/**
 * 非结构化健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class NonStructedProfile extends StructedProfile {
    // 文档列表
    private Map<String, RawDocument> documents;

    public Map<String, RawDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, RawDocument> documents) {
        this.documents = documents;
    }
}
