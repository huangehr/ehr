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
@Table(name = "icd10_indicator_relation")
@Access(value = AccessType.PROPERTY)
public class Icd10IndicatorRelation implements Serializable{

    public Icd10IndicatorRelation() {
    }

    private long id;
    private long icd10Id;
    private long indicatorId;
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

    @Column(name = "icd10_id", nullable = false)
    public long getIcd10Id() {
        return icd10Id;
    }
    public void setIcd10Id(long icd10Id) {
        this.icd10Id = icd10Id;
    }

    @Column(name = "indicator_id", nullable = false)
    public long getIndicatorId() {
        return indicatorId;
    }
    public void setIndicatorId(long indicatorId) {
        this.indicatorId = indicatorId;
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