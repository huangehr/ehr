package com.yihu.ehr.dict.service;


import com.yihu.ehr.util.phonics.PinyinUtil;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 14:43
 */
@Entity
@Table(name = "system_dicts")
@Access(value = AccessType.PROPERTY)
public class SystemDict {
    long id;
    String name;
    String reference;
    String authorId;
    String phoneticCode;
    Date createDate = new Date();

    public SystemDict(){
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
        this.phoneticCode = PinyinUtil.getPinYinHeadChar(name, true);
    }

    @Column(name = "reference", nullable = true)
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    @Column(name = "author", nullable = false)
    public String getAuthorId() {
        return authorId;
    }
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Column(name = "phonetic_code", nullable = false)
    public String getPhoneticCode() {
        return phoneticCode;
    }
    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }

    @Column(name = "create_date", nullable = false)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
