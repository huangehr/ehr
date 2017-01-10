package com.yihu.ehr.profile.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:42
 */
@Entity
@Table(name = "rs_medication_list")
@Access(value = AccessType.PROPERTY)
public class MedicationStat {
    private int drugId;
    private String drugName;
    private int month3;
    private int month6;
    private String code;
    private String unit;
    private String specifications;
    private Date lastTime = new Date();

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "drug_id")
    public int getDrugId() {
        return drugId;
    }

    public void setDrugId(int drugId) {
        this.drugId = drugId;
    }

    @Column(name = "drug_name")
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    @Column(name = "month3")
    public int getMonth3() {
        return month3;
    }

    public void setMonth3(int month3) {
        this.month3 = month3;
    }

    @Column(name = "month6")
    public int getMonth6() {
        return month6;
    }

    public void setMonth6(int month6) {
        this.month6 = month6;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "unit")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(name = "specifications")
    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    @Column(name = "last_time")
    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
