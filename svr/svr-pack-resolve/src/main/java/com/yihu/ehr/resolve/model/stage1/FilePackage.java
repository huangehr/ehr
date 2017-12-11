package com.yihu.ehr.resolve.model.stage1;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.core.ResourceCore;

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
@Table(ResourceCore.MasterTable)
public class FilePackage extends StandardPackage {
    public FilePackage() {
        setProfileType(ProfileType.File);
    }

    // 文档列表，Key为数据库主键
    private Map<String, CdaDocument> cdaDocuments = new TreeMap<>();

    public Map<String, CdaDocument> getCdaDocuments() {
        return cdaDocuments;
    }

    public void regularRowKey() {
        super.regularRowKey();

        int i = 0;
        Set<String> rowkeys = new HashSet<>(cdaDocuments.keySet());
        for (String rowkey : rowkeys){
            CdaDocument document = cdaDocuments.remove(rowkey);
            cdaDocuments.put(getId() + "$" + i++, document);
        }
    }

    public String getFileIndices(){
        return String.join(";", cdaDocuments.keySet());
    }

    protected ObjectNode jsonFormat() {
        ObjectNode root = super.jsonFormat();
        if (cdaDocuments != null) {
            ArrayNode files = root.putArray("documents");
            for (String key : cdaDocuments.keySet()) {
                files.add(cdaDocuments.get(key).toJson());
            }
        }

        return root;
    }
}
