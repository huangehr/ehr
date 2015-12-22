package com.yihu.ehr.std.model;

/**
 * Created by AndyCai on 2015/9/7.
 */
public class StandardSourceForInterface {

    private String code;
    private String description;
    private String id;
    private String name;
    /**
     * 标准类型  例如：门诊、住院
     */
    private String source_type;
    private String source_type_name;
    private String Version_Code;


    public StandardSourceForInterface(){
    }

    public void finalize() throws Throwable {

    }


    public String getSourceTypeName(){
        return this.source_type_name;
    }

    /**
     *
     * @param source_type_name
     */
    public void setSourceTypeName(String source_type_name){
        this.source_type_name=source_type_name;
    }

    public String getSourceType(){
        return this.source_type;
    }

    /**
     *
     * @param source_type
     */
    public void setSourceType(String source_type){
        this.source_type=source_type;
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

    public String getVersion_Code() {
        return this.Version_Code;
    }

    public void setVersion_Code(String version_Code) {
        this.Version_Code = version_Code;
    }

}
