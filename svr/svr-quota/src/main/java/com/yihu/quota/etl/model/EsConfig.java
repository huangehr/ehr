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
    private String fullQuery;  //全量查询
    private String filter;  // where条件
    private String timekey;//根据sql去查询的key 时间控制字段
    private String aggregation;// 聚合方式，默认count：计数；sum：求和；list：查询；distinct：去重查询；
    private String aggregationKey;//聚合字段
    private String especialType; //特殊类型  orgHealthCategory：卫生机构类型
    private String superiorBaseQuotaCode;  // 上级基础指标code
    private String dateComparisonType;      //时间对比类型  lastYear 去年 lastMonth 上个月 lastWeek 上个星期 lastDay 昨天

    // 去重查询
    private String distinctGroupField; // 分组去重字段
    private String distinctGroupSort; // 去重组内排序，如：“event_date asc”。
    private Boolean distinctGroupNullIsolate; // 去重组内，空值记录是保存第一条，还是每条记录单独保存，true：拆分保存，false：保存第一条，默认合并保存。

    //除法运算
    private String molecular;  // 统计除法的分子  指标code
    private String molecularFilter;  // 除法的分子过滤条件
    private String denominator;// 统计除法的分母  指标code
    private String denominatorFilter;// 除法的分母过滤条件
    private String percentOperation;//运算方式  1 乘法  2 除法
    private String percentOperationValue;//运算对应的值
    private String divisionType; //除法运算类型 1 分子分母各维度对应相除 默认 2 分子按维度 /分母按年份获取总数 如：技术人员每千人口 = 技术人员数/对应区县总人口数*1000

    //加法运算
    private String addOperation;//加法运算方式  1 加法 默认 2 减法
    private String addFirstQuotaCode;//加法第一个指标
    private String addFirstFilter;//加法第一个指标过滤条件
    private String addSecondQuotaCode;//加法第二个指标
    private String addSecondFilter;//加法第二个指标过滤条件

    private String growthFlag;  // 增幅标志  1 year  2 month
    private String incrementFlag;   // 环比  1 上月  2 本月



    //已停止使用
    private String thousandFlag; //每千，每万 1000,10000
    private String thousandDmolecular; //每千，每万 统计分子
    private String thousandDenominator; //每千，每万 统计分母

    public String getSuperiorBaseQuotaCode() {
        return superiorBaseQuotaCode;
    }

    public void setSuperiorBaseQuotaCode(String superiorBaseQuotaCode) {
        this.superiorBaseQuotaCode = superiorBaseQuotaCode;
    }

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

    public String getPercentOperation() {
        return percentOperation;
    }

    public void setPercentOperation(String percentOperation) {
        this.percentOperation = percentOperation;
    }

    public String getPercentOperationValue() {
        return percentOperationValue;
    }

    public void setPercentOperationValue(String percentOperationValue) {
        this.percentOperationValue = percentOperationValue;
    }

    public String getMolecularFilter() {
        return molecularFilter;
    }

    public void setMolecularFilter(String molecularFilter) {
        this.molecularFilter = molecularFilter;
    }

    public String getDenominatorFilter() {
        return denominatorFilter;
    }

    public void setDenominatorFilter(String denominatorFilter) {
        this.denominatorFilter = denominatorFilter;
    }

    public String getFullQuery() {
        return fullQuery;
    }

    public void setFullQuery(String fullQuery) {
        this.fullQuery = fullQuery;
    }

    public String getAddOperation() {
        return addOperation;
    }

    public void setAddOperation(String addOperation) {
        this.addOperation = addOperation;
    }

    public String getAddFirstQuotaCode() {
        return addFirstQuotaCode;
    }

    public void setAddFirstQuotaCode(String addFirstQuotaCode) {
        this.addFirstQuotaCode = addFirstQuotaCode;
    }

    public String getAddFirstFilter() {
        return addFirstFilter;
    }

    public void setAddFirstFilter(String addFirstFilter) {
        this.addFirstFilter = addFirstFilter;
    }

    public String getAddSecondQuotaCode() {
        return addSecondQuotaCode;
    }

    public void setAddSecondQuotaCode(String addSecondQuotaCode) {
        this.addSecondQuotaCode = addSecondQuotaCode;
    }

    public String getAddSecondFilter() {
        return addSecondFilter;
    }

    public void setAddSecondFilter(String addSecondFilter) {
        this.addSecondFilter = addSecondFilter;
    }

    public String getGrowthFlag() {
        return growthFlag;
    }

    public void setGrowthFlag(String growthFlag) {
        this.growthFlag = growthFlag;
    }

    public String getIncrementFlag() {
        return incrementFlag;
    }

    public void setIncrementFlag(String incrementFlag) {
        this.incrementFlag = incrementFlag;
    }

    public String getDivisionType() {
        return divisionType;
    }

    public void setDivisionType(String divisionType) {
        this.divisionType = divisionType;
    }

    public String getDateComparisonType() {
        return dateComparisonType;
    }

    public void setDateComparisonType(String dateComparisonType) {
        this.dateComparisonType = dateComparisonType;
    }

    public String getDistinctGroupField() {
        return distinctGroupField;
    }

    public void setDistinctGroupField(String distinctGroupField) {
        this.distinctGroupField = distinctGroupField;
    }

    public String getDistinctGroupSort() {
        return distinctGroupSort;
    }

    public void setDistinctGroupSort(String distinctGroupSort) {
        this.distinctGroupSort = distinctGroupSort;
    }

    public Boolean getDistinctGroupNullIsolate() {
        return distinctGroupNullIsolate;
    }

    public void setDistinctGroupNullIsolate(Boolean distinctGroupNullIsolate) {
        this.distinctGroupNullIsolate = distinctGroupNullIsolate;
    }
}
