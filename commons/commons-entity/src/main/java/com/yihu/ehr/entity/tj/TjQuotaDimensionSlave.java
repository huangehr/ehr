package com.yihu.ehr.entity.tj;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 统计细纬度关联表
 *
 * @author janseny
 * @version 1.0
 * @updated 2017年6月8日
 */
@Entity
@Table(name = "tj_quota_dimension_slave")
@Access(value = AccessType.PROPERTY)
public class TjQuotaDimensionSlave implements Serializable{


    private long id;
    private String quotaCode;  //关联 tj_quota code
    private String slaveCode; //关联 jt_dimension_slave
    private String dictSql;//
    private String key;//
    private Integer sort;//纬度顺序

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "quota_code",  nullable = true)
    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    @Column(name = "slave_code",  nullable = true)
    public String getSlaveCode() {
        return slaveCode;
    }

    public void setSlaveCode(String slaveCode) {
        this.slaveCode = slaveCode;
    }

    @Column(name = "dict_sql",  nullable = true)
    public String getDictSql() {
        return dictSql;
    }

    public void setDictSql(String dictSql) {
        this.dictSql = dictSql;
    }

    @Column(name = "key",  nullable = true)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}