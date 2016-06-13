package com.yihu.ehr.specialdict.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ICD10特殊字典管理.
 *
 * @author cws
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
@Entity
@Table(name = "hp_icd10_relation")
@Access(value = AccessType.PROPERTY)
public class Icd10HpRelation implements Serializable{

    public Icd10HpRelation() {
    }

    private long id;
    private String hpId;
    private String icd10Id;
    private String createUser;
    private Date createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "hp_id", nullable = false)
    public String getHpId() {
        return hpId;
    }
    public void setHpId(String hpId) {
        this.hpId = hpId;
    }

    @Column(name = "icd10_id", nullable = false)
    public String getIcd10Id() {
        return icd10Id;
    }
    public void setIcd10Id(String icd10Id) {
        this.icd10Id = icd10Id;
    }

    @Column(name = "create_user",  nullable = true)
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(name = "create_date",  nullable = true)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}