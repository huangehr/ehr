package com.yihu.ehr.service.resource.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.datetime.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class CdaDocument {
    String id;
    String name;
    List<OriginFile> originFiles = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OriginFile> getOriginFiles() {
        return originFiles;
    }

    public ObjectNode toJson(){
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        ObjectNode parent = objectMapper.createObjectNode();
        parent.put("id", id);
        parent.put("name", name);

        ArrayNode docList = parent.putArray("list");
        for (OriginFile originFile : originFiles){
            ObjectNode objectNode = docList.addObject();
            objectNode.put("mime", originFile.getMime());
            objectNode.put("urls", originFile.getUrlsStr());
            objectNode.put("url_scope", originFile.getUrlScope().name());
            objectNode.put("expireDate", originFile.getExpireDate()==null ? null: DateTimeUtil.simpleDateFormat(originFile.getExpireDate()));
            objectNode.put("emr_id",originFile.getEmrId());
            objectNode.put("emr_name",originFile.getEmrName());
            objectNode.put("note",originFile.getNote());
            ObjectNode listNode = objectNode.putObject("files");
            Map<String, String> files = originFile.getFileUrls();
            for (String fileName : files.keySet()){
                listNode.put(fileName, files.get(fileName));
            }
        }

        return parent;
    }
}