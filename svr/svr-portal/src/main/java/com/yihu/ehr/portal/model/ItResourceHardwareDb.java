package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "it_resource_hardware_db")
@Access(value = AccessType.FIELD)
public class ItResourceHardwareDb {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "master_id", unique = true, nullable = false)
    private int masterId;

    @Column(name = "purpose", unique = true, nullable = false)
    private String purpose;

    @Column(name = "apply_qty", unique = true, nullable = false)
    private int applyQty;

    @Column(name = "db_type", unique = true, nullable = false)
    private String dbType;

    @Column(name = "data_rise", unique = true, nullable = false)
    private String dataRise;

    public ItResourceHardwareDb() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(int applyQty) {
        this.applyQty = applyQty;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDataRise() {
        return dataRise;
    }

    public void setDataRise(String dataRise) {
        this.dataRise = dataRise;
    }
}