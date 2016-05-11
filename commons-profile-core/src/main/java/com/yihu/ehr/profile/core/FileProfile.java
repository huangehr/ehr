package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.util.ProfileUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 文件型健康档案（非结构化）。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
@Table(ProfileUtil.Table)
public class FileProfile extends StdProfile {
    public FileProfile() {
        setProfileType(ProfileType.File);
    }

    private Map<String, CdaDocument> documents = new TreeMap<>();   // 文档列表，Key为数据库主键

    public Map<String, CdaDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Map<String, CdaDocument> documents) {
        this.documents = documents;
    }

    public void regularRowKey() {
        super.regularRowKey();

        int i = 0;
        Set<String> rowkeys = new HashSet<>(documents.keySet());
        for (String rowkey : rowkeys){
            CdaDocument document = documents.remove(rowkey);
            documents.put(getId() + "$" + i++, document);
        }
    }

    public String getFileIndices(){
        return String.join(";", documents.keySet());
    }

    protected ObjectNode jsonFormat() {
        ObjectNode root = super.jsonFormat();
        if (documents != null) {
            ArrayNode files = root.putArray("documents");
            for (String key : documents.keySet()) {
                files.add(documents.get(key).toJson());
            }
        }

        return root;
    }
}
