package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class RawDocumentList extends ArrayList<RawDocument>{
    public ArrayNode toJson(){
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ArrayNode parent = objectMapper.createArrayNode();
        for (RawDocument document : this){
            ObjectNode objectNode = parent.addObject();
            objectNode.put("id", document.getCdaDocumentId());
            objectNode.put("originUrl", document.getOriginUrl());
            objectNode.put("expireDate", DateTimeUtils.simpleDateFormat(document.getExpireDate()));

            ObjectNode listNode = objectNode.putObject("files");
            Map<String, String> files = document.getStorageUrls();
            for (String fileName : files.keySet()){
                listNode.put(fileName, files.get(fileName));
            }
        }

        return parent;
    }
}