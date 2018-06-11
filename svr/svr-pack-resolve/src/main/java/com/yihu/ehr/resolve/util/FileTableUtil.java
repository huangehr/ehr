package com.yihu.ehr.resolve.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.details.CdaDocument;
import com.yihu.ehr.resolve.model.stage1.details.LinkFile;
import com.yihu.ehr.resolve.model.stage1.details.OriginFile;
import com.yihu.ehr.util.datetime.DateTimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.04.15
 */
public class FileTableUtil {

    public static Map<String, String> getBasicFamilyCellMap(OriginalPackage originalPackage) {
        Map<String, String> map = new HashMap<>();
        map.put(ResourceCells.PATIENT_ID, originalPackage.getPatientId());
        map.put(ResourceCells.EVENT_NO, originalPackage.getEventNo());
        map.put(ResourceCells.ORG_CODE, originalPackage.getOrgCode());
        return map;
    }

    public static Map<String, String> getFileFamilyCellMap(CdaDocument cdaDocument) {
        ArrayNode root = (SpringContext.getService(ObjectMapper.class)).createArrayNode();
        for (OriginFile originFile : cdaDocument.getOriginFiles()) {
            ObjectNode subNode = root.addObject();
            subNode.put("mime", originFile.getMime());
            subNode.put("urls", originFile.getUrlsStr());
            String name = originFile.getUrlScope()==null ? "":originFile.getUrlScope().name();
            subNode.put("url_score", name);
            subNode.put("emr_id", originFile.getEmrId());
            subNode.put("emr_name", originFile.getEmrName());
            subNode.put("expire_date", originFile.getExpireDate()== null ? "" : DateTimeUtil.utcDateTimeFormat(originFile.getExpireDate()));
            subNode.put("note", originFile.getNote());
            StringBuilder builder = new StringBuilder();
            for (String fileName : originFile.getFileUrls().keySet()){
                builder.append(fileName).append(":").append(originFile.getFileUrls().get(fileName)).append(";");
            }
            subNode.put("files", builder.toString());
        }
        Map<String, String> map = new HashMap<>();
        map.put(ResourceCells.CDA_DOCUMENT_ID, cdaDocument.getId());
        map.put(ResourceCells.CDA_DOCUMENT_NAME, cdaDocument.getName());
        map.put(ResourceCells.FILE_LIST, root.toString());
        return map;
    }

    public static Map<String, String> getFileFamilyCellMap(LinkPackage linkPackage){
        Map<String, String> map = new HashMap<>();
        if (null == linkPackage){
            return map;
        }
        List<LinkFile> linkFiles = linkPackage.getLinkFiles();
        ArrayNode root = (SpringContext.getService(ObjectMapper.class)).createArrayNode();
        for (LinkFile linkFile:linkFiles){
            ObjectNode subNode = root.addObject();
            subNode.put("url", linkFile.getUrl());
            subNode.put("originName", linkFile.getOriginName());
            subNode.put("fileExtension", linkFile.getFileExtension());
            subNode.put("fileSize", linkFile.getFileSize());
            root.add(subNode);
        }
        map.put(ResourceCells.FILE_LIST, root.toString());
        return map;
    }
}
