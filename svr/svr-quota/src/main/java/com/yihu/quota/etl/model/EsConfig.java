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
    private String timekey;//根据sql去查询的key
    private String config;  // where条件
    private String aggregation;//聚合方式  默认count，另有sum
    private String aggregationKey;//聚合字段

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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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
}
