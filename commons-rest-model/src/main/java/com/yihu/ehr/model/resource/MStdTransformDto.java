package com.yihu.ehr.model.resource;

/**
 * @author linaz
 * @created 2016.06.13 17:11
 */
public class MStdTransformDto {
    private String version;
    private String source;
    private String dataset;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}