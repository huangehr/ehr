package com.yihu.ehr.standard.dict.service;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class DictForInterface{

    public DictForInterface(){

    }

    /**
     * 获取ID.
     */
    String id;
    public String getId(){
        return id;
    }
    public void setId(String dictid){
        this.id=dictid;
    }

    //字典代码
    String code;
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }

    //字典名称
    String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    // 作者
    String author;
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author=author;
    }

    //参考/继承的字典
    String baseDict;
    public String getBaseDictId(){
        return baseDict;
    }
    public void setBaseDictId(String baseDictId){
        this.baseDict=baseDictId;
    }

    //创建日期
    String createdate;
    public String getCreateDate(){
        return createdate;
    }
    public void setCreateDate(String createDate){
        this.createdate=createDate;
    }

    //字典描述
    String description;
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }



    public String getInnerVersionId() {
        return innerVersionId;
    }

    public void setInnerVersionId(String innerVersionId) {
        this.innerVersionId = innerVersionId;
    }

    String innerVersionId;

    //是否处于版本化编辑状态
    String inStage;
    public String getInStage(){return inStage;}
    public void setInStage(String inStage){this.inStage=inStage;}


    /**
     * 标准化版本
     */
    String stdVersion;
    public String getStdVersion(){
        return stdVersion;
    }
    public void setStdVersion(String version){
        this.stdVersion=version;
    }

    String hashCode;
    public String getHashCode() {
        return hashCode;
    }
    public void setHashCode(String hashCode){
        this.hashCode = hashCode;
    }

    /**
     * 标准化版本
     */
    String stdSource;

    public String getStdSource() {
        return stdSource;
    }

    public void setStdSource(String stdSource) {
        this.stdSource = stdSource;
    }
}
