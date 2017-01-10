package com.yihu.ehr.agModel.standard.datasset;

/**
 * Created by AndyCai on 2016/2/29.
 */
public class DataSetModel {

    private long id;

    private String code;

    private String name;

    private String stdVersion;

    private String reference;
    private String referenceCode;

    private String summary;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStdVersion() {
        return stdVersion;
    }

    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
