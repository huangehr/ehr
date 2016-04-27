package com.yihu.ehr.profile.core;

import java.text.ParseException;
import java.util.Map;

/**
 * 非结构化健康档案。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class NonStructedProfile extends StructedProfile {
    // 文档列表，Key为CDA文档ID
    private Map<String, RawDocumentList> documents;

    public Map<String, RawDocumentList> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, RawDocumentList> documents) {
        this.documents = documents;
    }
}
