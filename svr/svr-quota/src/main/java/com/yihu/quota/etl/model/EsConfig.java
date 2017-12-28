package com.yihu.quota.etl.model;

/**
 * Created by chenweida on 2017/6/2.
 */
public class EsConfig {

    private String host;//地址
    private Integer port;//端口
    private String index;// 索引 es相当与数据库
    private String type;// 类型 es 相当于表
    private String clusterName;//es clusterName
    private String table;// 数据库表
    private String molecular;  // 统计百分比时的分子  指标code
    private String denominator;// 统计百分比时的分母  指标code
    private String timekey;//根据sql去查询的key 时间控制字段
    private String filter;  // where条件
    private String aggregation;//聚合方式  默认count，另有sum
    private String aggregationKey;//聚合字段

    private String thousandFlag; //每千，每万 1000,10000
    private String thousandDmolecular; //每千，每万 统计分子
    private String thousandDenominator; //每千，每万 统计分母

    private String especialType; //特殊类型  orgHealthCategory：卫生机构类型

    private String superiorCode;//上级指标code

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTimekey() {
        return timekey;
    }

    public void setTimekey(String timekey) {
        this.timekey = timekey;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getMolecular() {
        return molecular;
    }

    public void setMolecular(String molecular) {
        this.molecular = molecular;
    }

    public String getDenominator() {
        return denominator;
    }

    public void setDenominator(String denominator) {
        this.denominator = denominator;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getAggregationKey() {
        return aggregationKey;
    }

    public void setAggregationKey(String aggregationKey) {
        this.aggregationKey = aggregationKey;
    }


    public String getThousandFlag() {
        return thousandFlag;
    }

    public void setThousandFlag(String thousandFlag) {
        this.thousandFlag = thousandFlag;
    }

    public String getThousandDmolecular() {
        return thousandDmolecular;
    }

    public void setThousandDmolecular(String thousandDmolecular) {
        this.thousandDmolecular = thousandDmolecular;
    }

    public String getThousandDenominator() {
        return thousandDenominator;
    }

    public void setThousandDenominator(String thousandDenominator) {
        this.thousandDenominator = thousandDenominator;
    }


    public String getEspecialType() {
        return especialType;
    }

    public void setEspecialType(String especialType) {
        this.especialType = especialType;
    }

    public String getSuperiorCode() {
        return superiorCode;
    }

    public void setSuperiorCode(String superiorCode) {
        this.superiorCode = superiorCode;
    }
}
