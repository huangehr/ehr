package com.yihu.ehr.dict.service;

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
@Table(name = "icd10_drug_relation")
@Access(value = AccessType.PROPERTY)
public class Icd10DrugRelation implements Serializable{

    public Icd10DrugRelation() {
    }

    private String id;
    private String icd10Id;
    private String drugId;

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

    @Column(name = "icd10_id", nullable = false)
    public String getIcd10Id() {
        return icd10Id;
    }
    public void setIcd10Id(String icd10Id) {
        this.icd10Id = icd10Id;
    }

    @Column(name = "drug_id", nullable = false)
    public String getDrugId() {
        return drugId;
    }
    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }
}