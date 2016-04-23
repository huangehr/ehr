package com.yihu.ehr.agModel.patient;

/**
 * Created by AndyCai on 2016/4/20.
 */
public class HomeRelationshipModel {

    String id;
    String name;
    String age;
    String relationShipName;
    String relationTime;

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRelationShipName() {
        return relationShipName;
    }

    public void setRelationShipName(String relationShipName) {
        this.relationShipName = relationShipName;
    }

    public String getRelationTime() {
        return relationTime;
    }

    public void setRelationTime(String relationTime) {
        this.relationTime = relationTime;
    }
}
