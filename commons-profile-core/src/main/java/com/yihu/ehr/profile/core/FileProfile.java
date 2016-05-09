package com.yihu.ehr.profile.core;

import java.util.Map;

/**
 * 文件型健康档案（非结构化）。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class FileProfile extends StdProfile {
    // 文档列表，Key为CDA文档ID
    private Map<String, RawDocumentList> documents;

    public Map<String, RawDocumentList> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, RawDocumentList> documents) {
        this.documents = documents;
    }
}
