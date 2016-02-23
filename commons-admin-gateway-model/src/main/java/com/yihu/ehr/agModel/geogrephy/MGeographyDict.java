package com.yihu.ehr.agModel.geogrephy;

/**
 * Created by AndyCai on 2016/2/16.
 */
public class MGeographyDict {

    private int id;   //序号

    private String abbreviation;    //简称

    private int level;  //级别

    private String name;  //名字

    private int pid; //上级序号

    private String postCode;  //邮政编码

    public MGeographyDict(){
        //id  = Integer.parseInt(UUID.randomUUID().toString());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
