package com.yihu.ehr.dict.model;

import com.yihu.ehr.util.PinyinUtil;

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
@Table(name = "drug_dict")
@Access(value = AccessType.PROPERTY)
public class DrugDict implements Serializable{

    public DrugDict() {
    }

    private long id;
    private String code;
    private String name;
    private String phoneticCode;
    private String type;
    private String flag;
    private String tradeName;
    private String unit;
    private String specifications;
    private String description;
    private String createUser;
    private Date createDate;
    private String updateUser;
    private Date updateDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "code",  nullable = true)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
        this.phoneticCode = PinyinUtil.getPinYinHeadChar(name, true);
    }

    @Column(name = "phonetic_code", nullable = false)
    public String getPhoneticCode() {
        return phoneticCode;
    }
    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }

    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "flag", nullable = false)
    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Column(name = "trade_name", nullable = false)
    public String getTradeName() {
        return tradeName;
    }
    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    @Column(name = "unit", nullable = false)
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(name = "specifications", nullable = false)
    public String getSpecifications() {
        return specifications;
    }
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    @Column(name = "description",  nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    @Column(name = "update_user",  nullable = true)
    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(name = "update_date",  nullable = true)
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}