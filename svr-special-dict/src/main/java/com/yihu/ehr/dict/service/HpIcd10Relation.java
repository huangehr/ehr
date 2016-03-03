package com.yihu.ehr.dict.service;

import com.yihu.ehr.util.ObjectVersion;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

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
public class HpIcd10Relation implements Serializable{

    public HpIcd10Relation() {
    }

    private String id;
    private String hpId;
    private String icd10Id;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
}