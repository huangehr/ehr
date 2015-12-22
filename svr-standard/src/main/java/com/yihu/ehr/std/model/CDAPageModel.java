package com.yihu.ehr.std.model;

/**
 * Created by AndyCai on 2015/9/7.
 */
public class CDAPageModel {
    private String code;
    private String description;
    private String id;
    private String name;
    private String versionCode;
    /**
     * 输出排版路径
     */
    private String printOut;
    private String schema;
    /**
     * 标准来源ID
     */
    private String sourceId;

    private String user;


    public CDAPageModel(){

    }

    public void finalize() throws Throwable {

    }
    public String getPrintOut(){
        return this.printOut;
    }

    public String getSchema(){
        return this.schema;
    }

    public String getSourceId(){
        return this.sourceId;
    }

    /**
     *
     * @param print_out
     */
    public void setPrintOut(String print_out){
        this.printOut=print_out;
    }

    /**
     *
     * @param schema
     */
    public void setSchema(String schema){
        this.schema=schema;
    }

    /**
     *
     * @param source_id
     */
    public void setSourceId(String source_id){
        this.sourceId=source_id;
    }

    public String getCode(){
        return this.code;
    }

    public String getDescription(){
        return this.description;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    /**
     *
     * @param code
     */
    public void setCode(String code){
        this.code=code;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description){
        this.description=description;
    }

    /**
     *
     * @param id
     */
    public void setId(String id){
        this.id=id;
    }

    /**
     *
     * @param name
     */
    public void setName(String name){
        this.name=name;
    }

    public void setVersionCode(String versionCode){
        this.versionCode=versionCode;
    };

    public String getVersionCode(){
        return this.versionCode;
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
