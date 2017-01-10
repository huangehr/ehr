package com.yihu.ehr.agModel.standard.cdadocument;

/**
 * Created by wq on 2016/3/4.
 */
public class CdaDataSetRelationshipModel {

    private String id;

    private String cdaId;

    private String dataSetId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdaId() {
        return cdaId;
    }

    public void setCdaId(String cdaId) {
        this.cdaId = cdaId;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }
}
