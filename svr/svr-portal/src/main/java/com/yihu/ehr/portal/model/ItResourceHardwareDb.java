package com.yihu.ehr.portal.model;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "it_resource_hardware_db", schema = "", catalog = "healtharchive")
public class ItResourceHardwareDb {
    private int id;
    private Integer masterId;
    private String purpose;
    private Integer applyQty;
    private String dbType;
    private Integer dataRise;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "master_id", nullable = true, insertable = true, updatable = true)
    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    @Basic
    @Column(name = "purpose", nullable = true, insertable = true, updatable = true)
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Basic
    @Column(name = "apply_qty", nullable = true, insertable = true, updatable = true)
    public Integer getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(Integer applyQty) {
        this.applyQty = applyQty;
    }

    @Basic
    @Column(name = "db_type", nullable = true, insertable = true, updatable = true)
    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    @Basic
    @Column(name = "data_rise", nullable = true, insertable = true, updatable = true)
    public Integer getDataRise() {
        return dataRise;
    }

    public void setDataRise(Integer dataRise) {
        this.dataRise = dataRise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItResourceHardwareDb that = (ItResourceHardwareDb) o;

        if (id != that.id) return false;
        if (masterId != null ? !masterId.equals(that.masterId) : that.masterId != null) return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (applyQty != null ? !applyQty.equals(that.applyQty) : that.applyQty != null) return false;
        if (dbType != null ? !dbType.equals(that.dbType) : that.dbType != null) return false;
        if (dataRise != null ? !dataRise.equals(that.dataRise) : that.dataRise != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (masterId != null ? masterId.hashCode() : 0);
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (applyQty != null ? applyQty.hashCode() : 0);
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        result = 31 * result + (dataRise != null ? dataRise.hashCode() : 0);
        return result;
    }
}
