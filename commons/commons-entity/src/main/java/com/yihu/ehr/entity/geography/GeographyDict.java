package com.yihu.ehr.entity.geography;

import javax.persistence.*;

/**
 * 地址字典
 * @author zqb
 * @version 1.0
 * @created 30-六月-2015 15:59:31
 */
@Entity
@Table(name = "address_dict")
@Access(value = AccessType.PROPERTY)
public class GeographyDict {


    private long id;   //序号

    private String abbreviation;    //简称

    private int level;  //级别

    private String name;  //名字

    private int pid; //上级序号

    private String postCode;  //邮政编码

    public GeographyDict(){
        //id  = Integer.parseInt(UUID.randomUUID().toString());
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "abbreviation", nullable = true)
    public String getAbbreviation() {
        return abbreviation;
    }
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Column(name = "level",  nullable = false)
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    @Column(name = "name",  nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "pid",  nullable = true)
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }

    @Column(name = "post_code",  nullable = true)
    public String getPostcode() {
        return postCode;
    }
    public void setPostcode(String postCode) {
        this.postCode = postCode;
    }
}