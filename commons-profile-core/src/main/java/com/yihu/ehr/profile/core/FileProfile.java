package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 文件型健康档案（非结构化）。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class FileProfile extends StdProfile {
    public FileProfile() {
        setProfileType(ProfileType.File);
    }

    // 文档列表，Key为CDA文档ID
    private Map<String, RawDocumentList> documents = new TreeMap<>();

    public Map<String, RawDocumentList> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, RawDocumentList> documents) {
        this.documents = documents;
    }

    protected ObjectNode jsonFormat() {
        ObjectNode root = super.jsonFormat();
        if (documents != null) {
            ArrayNode files = root.putArray("documents");
            for (String key : documents.keySet()) {
                files.addAll(documents.get(key).toJson());
            }
        }

        return root;
    }
}
