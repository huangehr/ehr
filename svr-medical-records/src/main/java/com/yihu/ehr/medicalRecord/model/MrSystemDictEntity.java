package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_system_dict", schema = "", catalog = "medical_records")
public class MrSystemDictEntity {
    private String dictCode;
    private String dictName;
    private String description;

    @Id
    @Column(name = "DICT_CODE")
    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    @Basic
    @Column(name = "DICT_NAME")
    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrSystemDictEntity that = (MrSystemDictEntity) o;

        if (dictCode != null ? !dictCode.equals(that.dictCode) : that.dictCode != null) return false;
        if (dictName != null ? !dictName.equals(that.dictName) : that.dictName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dictCode != null ? dictCode.hashCode() : 0;
        result = 31 * result + (dictName != null ? dictName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
