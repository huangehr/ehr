package com.yihu.ehr.standard.dict.service;

/**
 * Created by AndyCai on 2015/8/11.
 */
public class DictForInterface{

    public DictForInterface(){

    }

    /**
     * ��ȡID.
     */
    String id;
    public String getId(){
        return id;
    }
    public void setId(String dictid){
        this.id=dictid;
    }

    //�ֵ����
    String code;
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }

    //�ֵ�����
    String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    // ����
    String author;
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author=author;
    }

    //�ο�/�̳е��ֵ�
    String baseDict;
    public String getBaseDictId(){
        return baseDict;
    }
    public void setBaseDictId(String baseDictId){
        this.baseDict=baseDictId;
    }

    //��������
    String createdate;
    public String getCreateDate(){
        return createdate;
    }
    public void setCreateDate(String createDate){
        this.createdate=createDate;
    }

    //�ֵ�����
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

    //�Ƿ��ڰ汾���༭״̬
    String inStage;
    public String getInStage(){return inStage;}
    public void setInStage(String inStage){this.inStage=inStage;}


    /**
     * ��׼���汾
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
     * ��׼���汾
     */
    String stdSource;

    public String getStdSource() {
        return stdSource;
    }

    public void setStdSource(String stdSource) {
        this.stdSource = stdSource;
    }
}
