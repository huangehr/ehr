package com.yihu.ehr.std.model;

/**
 * Created by AndyCai on 2015/9/23.
 */
public class CDADatasetRelationshipPageModel {
    private String cda_id;
    private String id;
    private String set_id;
    private String version_code;

    private String dataset_code;
    private String dataset_name;
    private String summary;

    public String getDataset_code() {
        return dataset_code;
    }

    public void setDataset_code(String dataset_code) {
        this.dataset_code = dataset_code;
    }

    public String getDataset_name() {
        return dataset_name;
    }

    public void setDataset_name(String dataset_name) {
        this.dataset_name = dataset_name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public CDADatasetRelationshipPageModel(){

    }

    public void finalize() throws Throwable {

    }

    public String getCdaId(){
        return this.cda_id;
    }


    public String getDataset_id(){
        return this.set_id;
    }

    public String getId(){
        return this.id;
    }

    /**
     *
     * @param cda_id
     */
    public void setCdaId(String cda_id){
        this.cda_id=cda_id;
    }

    /**
     *
     * @param dataset_id
     */
    public void setDatasetId(String dataset_id){
        this.set_id=dataset_id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id){
        this.id=id;
    }

    public void setVersion_code(String version_code){
        this.version_code=version_code;
    }

    public String getVersion_code(){
        return this.version_code;
    }
}
