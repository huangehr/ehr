package com.yihu.ehr.medicalRecords.model.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by hzp on 2016/8/4.
 */
@Entity
@Table(name = "mr_medical_info")
public class MrMedicalInfoEntity {
    private int id;
    private String recordId;
    private String code;
    private String name;
    private String value;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "RECORD_ID")
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "VALUE")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
