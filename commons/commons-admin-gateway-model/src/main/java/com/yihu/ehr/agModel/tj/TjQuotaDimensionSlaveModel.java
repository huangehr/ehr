package com.yihu.ehr.agModel.tj;

import java.io.Serializable;

/**
 *
 * @author janseny
 * @version 1.0
 * @updated 2017年6月8日
 */

public class TjQuotaDimensionSlaveModel{


    private long id;
    private String quotaCode;    //关联 tj_quota code
    private String slaveCode;   //关联 jt_dimension_slave
    private String name;        //细纬度名称
    private String dictSql;
    private String key;
    private Integer sort;       //纬度顺序

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getSlaveCode() {
        return slaveCode;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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