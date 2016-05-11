package com.yihu.ehr.model.profile;

import com.yihu.ehr.profile.core.RawDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
public class MProfileDocument {
    private String id;                  // CDA文档ID
    private String name;                // CDA文档名称
    private Integer templateId;         // 展示模板ID

    private List<MDataSet> dataSets = new ArrayList<>();

    private List<MRawDocument> rawDocuments = new ArrayList<>();

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

    public List<MDataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<MDataSet> dataSets) {
        this.dataSets = dataSets;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public List<MRawDocument> getRawDocuments() {
        return rawDocuments;
    }

    public void setRawDocuments(List<MRawDocument> rawDocuments) {
        this.rawDocuments = rawDocuments;
    }
}
