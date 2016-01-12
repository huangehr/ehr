package com.yihu.ehr.address.service;

import javax.persistence.*;
import java.util.UUID;

/**
 * 地址字典
 * @author zqb
 * @version 1.0
 * @created 30-六月-2015 15:59:31
 */
@Entity
@Table(name = "address_dict")
@Access(value = AccessType.PROPERTY)
public class AddressDict {


    private Integer id;   //序号

    private String abbreviation;    //简称

    private Integer level;  //级别

    private String name;  //名字

    private Integer pid; //上级序号

    private String postCode;  //邮政编码

    public AddressDict(){
        id  = Integer.parseInt(UUID.randomUUID().toString());
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
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
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
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
    public Integer getPid() {
        return pid;
    }
    public void setPid(Integer pid) {
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