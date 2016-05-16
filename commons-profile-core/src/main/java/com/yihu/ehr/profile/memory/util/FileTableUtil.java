package com.yihu.ehr.profile.memory.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.memory.intermediate.CdaDocument;
import com.yihu.ehr.profile.memory.intermediate.FileFamily;
import com.yihu.ehr.profile.memory.intermediate.MemoryFileProfile;
import com.yihu.ehr.profile.memory.intermediate.OriginFile;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.04.15
 */
public class FileTableUtil {
    public static final String Table = "RawFiles";

    public static Map<String, String> getBasicFamilyCellMap(MemoryFileProfile profile) {
        Map<String, String> map = new HashMap<>();
        map.put(FileFamily.BasicColumns.PatientId, profile.getPatientId());
        map.put(FileFamily.BasicColumns.EventNo, profile.getEventNo());
        map.put(FileFamily.BasicColumns.OrgCode, profile.getOrgCode());

        return map;
    }

    public static Map<String, String> getFileFamilyCellMap(CdaDocument cdaDocument) {
        ArrayNode root = ((ObjectMapper) SpringContext.getService("objectMapper")).createArrayNode();
        for (OriginFile originFile : cdaDocument.getOriginFiles()) {
            ObjectNode subNode = root.addObject();
            subNode.put("mime", originFile.getMime());
            subNode.put("origin_url", originFile.getOriginUrl());
            subNode.put("expire_date", DateTimeUtils.utcDateTimeFormat(originFile.getExpireDate()));

            StringBuilder builder = new StringBuilder();
            for (String fileName : originFile.getFileUrls().keySet()){
                builder.append(fileName).append(":").append(originFile.getFileUrls().get(fileName)).append(";");
            }
            subNode.put("files", builder.toString());
        }

        Map<String, String> map = new HashMap<>();
        map.put(FileFamily.FileColumns.CdaDocumentId, cdaDocument.getId());
        map.put(FileFamily.FileColumns.CdaDocumentName, cdaDocument.getName());
        map.put(FileFamily.FileColumns.FileList, root.toString());

        return map;
    }
}
