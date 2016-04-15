package com.yihu.ehr.model.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
public class MProfileDocument {
    private String id;      // CDA文档ID
    private String name;    // CDA文档名称

    private List<MDataSet> dataSets = new ArrayList<>();

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
}
