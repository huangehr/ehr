package com.yihu.ehr.profile.core.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;

/**
 * 链接型数据集，保存少量数据元及数据集本身的链接。数据本身包含在机构档案中。
 *
 * @author Sand
 * @created 2016.04.26 19:31
 */
public class LinkDataSet extends StdDataSet {
    protected String url;           // 数据集相对路径

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");

        ObjectNode root = dataSetHeader(objectMapper);
        return dataSetBody(root);
    }

    protected ObjectNode dataSetHeader(ObjectMapper objectMapper){
        ObjectNode root = super.dataSetHeader(objectMapper);
        root.put("url", url);

        return root;
    }
}
