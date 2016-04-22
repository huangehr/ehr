package com.yihu.ehr.profile.core.nostructured;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.commons.Profile;
import net.minidev.json.JSONArray;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class UnStructuredProfile extends Profile{

    private ObjectMapper objectMapper = SpringContext.getService("objectMapper");

    //非结构化data数组内容
    private List<UnStructuredDocument> unStructuredDocumentList;

    public List<UnStructuredDocument> getUnStructuredDocumentList() {
        return unStructuredDocumentList;
    }

    public void setUnStructuredDocumentList(List<UnStructuredDocument> unStructuredDocumentList) {
        this.unStructuredDocumentList = unStructuredDocumentList;
    }

//    public String toJson() throws ParseException, JsonProcessingException {
//        ObjectNode root = objectMapper.createObjectNode();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        ArrayNode documentNode = root.putArray("unStructuredDocumentList");

//        objectMapper.writeValueAsString(unStructuredDocumentList);
//        String array = JSONArray.toJSONString(unStructuredDocumentList);
//        for (UnStructuredDocument unStructuredDocument : unStructuredDocumentList){
//            root.put("cda_doc_id", unStructuredDocument.getCdaDocId());
//            root.put("url", unStructuredDocument.getUrl());
//            root.put("expiry_date", sdf.format(unStructuredDocument.getExpiryDate()));
//            root.put("key_words_str", unStructuredDocument.getKeyWordsStr());
//
//            List<UnStructuredContent> unStructuredContentList = unStructuredDocument.getUnStructuredContentList();
//            ArrayNode unStructuredContentNode = root.putArray("unStructuredContentList");
//            for(UnStructuredContent unStructuredContent : unStructuredContentList){
//                ArrayNode unStructuredDocumentFileNode = root.putArray("unStructuredDocumentFileList");
//                List<UnStructuredDocumentFile> unStructuredDocumentFileList = unStructuredContent.getUnStructuredDocumentFileList();
//                for (UnStructuredDocumentFile unStructuredDocumentFile : unStructuredDocumentFileList){
//                    unStructuredDocumentFileNode.addPOJO(unStructuredDocumentFile);
//                }
//                unStructuredContentNode.addPOJO(unStructuredContent);
//            }
//
//            documentNode.addPOJO(unStructuredDocument);
//        }
//        return root.toString();
//    }

}
