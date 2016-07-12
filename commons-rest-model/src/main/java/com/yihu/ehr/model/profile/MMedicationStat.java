package com.yihu.ehr.model.profile;

import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:42
 */
public class MMedicationStat {
    private int drugId;
    private String drugName;
    private int month3;
    private int month6;
    private String code;
    private String unit;
    private String specifications;
    private Date lastTime = new Date();

    public int getDrugId() {
        return drugId;
    }

    public void setDrugId(int drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getMonth3() {
        return month3;
    }

    public void setMonth3(int month3) {
        this.month3 = month3;
    }

    public int getMonth6() {
        return month6;
    }

    public void setMonth6(int month6) {
        this.month6 = month6;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
