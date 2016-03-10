package com.yihu.ehr.standard.dict.service;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * 字典项
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
@MappedSuperclass
public class IDictEntry {

    long id;
    Long dictId;//字典ID
    String code;
    String value;
    String desc;
    int hashCode;

    String OperationType;

    public IDictEntry() {
        this.OperationType = "";
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "dict_id", unique = false, nullable = false)
    public long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    @Column(name = "code", unique = false, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "description", unique = false, nullable = true)
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Column(name = "value", unique = false, nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "hash", unique = false, nullable = true)
    public int getHashCode() {
        hashCode = Objects.hash(dictId, code, value, desc);
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    ;

    @Transient
    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

}