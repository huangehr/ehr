package com.yihu.quota.model.jpa.dimension;// default package

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TjQuotaDimensionSlave entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tj_quota_dimension_slave")
public class TjQuotaDimensionSlave implements java.io.Serializable {
    // Fields

    private Integer id;
    private String quotaCode;
    private String slaveCode;
    private String dictSql;//字典的sql
    private String keyVal;
    private String type;
    private String groupByKey;//group by用的key



    // Constructors

    /**
     * default constructor
     */
    public TjQuotaDimensionSlave() {
    }

    /**
     * full constructor
     */
    public TjQuotaDimensionSlave(String quotaCode, String slaveCode) {
        this.quotaCode = quotaCode;
        this.slaveCode = slaveCode;
    }

    // Property accessors
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "quota_code", length = 100)
    public String getQuotaCode() {
        return this.quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    @Column(name = "slave_code", length = 100)
    public String getSlaveCode() {
        return this.slaveCode;
    }

    public void setSlaveCode(String slaveCode) {
        this.slaveCode = slaveCode;
    }

    public String getDictSql() {
        return dictSql;
    }

    public void setDictSql(String dictSql) {
        this.dictSql = dictSql;
    }

    @Transient
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "key_val", length = 100)
    public String getKeyVal() {
        return keyVal;
    }

    public void setKeyVal(String keyVal) {
        this.keyVal = keyVal;
    }

    public String getGroupByKey() {
        return groupByKey;
    }

    public void setGroupByKey(String groupByKey) {
        this.groupByKey = groupByKey;
    }
}