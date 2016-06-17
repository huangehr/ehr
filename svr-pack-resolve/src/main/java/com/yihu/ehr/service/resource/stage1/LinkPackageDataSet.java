package com.yihu.ehr.service.resource.stage1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.util.PackageDataSet;

/**
 * 链接型数据集，保存少量数据元及数据集本身的链接。数据本身包含在机构档案中。
 *
 * @author Sand
 * @created 2016.04.26 19:31
 */
public class LinkPackageDataSet extends PackageDataSet {
    protected String url;           // 数据集相对路径

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);

        ObjectNode root = dataSetHeader(objectMapper);
        return dataSetBody(root);
    }

    protected ObjectNode dataSetHeader(ObjectMapper objectMapper){
        ObjectNode root = super.dataSetHeader(objectMapper);
        root.put("url", url);

        return root;
    }
}
