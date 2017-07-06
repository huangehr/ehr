package com.yihu.ehr.agModel.tj;


/**
 * @author janseny
 * @version 1.0
 * @updated 2017年6月8日
 */

public class TjQuotaDimensionMainModel
{


    private Long id;
    private String quotaCode;       //关联 tj_quota code
    private String mainCode;        //关联 jt_dimension_main
    private String name;            //主纬度名称
    private String dictSql;
    private String keyVal;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getMainCode() {
        return mainCode;
    }

    public void setMainCode(String mainCode) {
        this.mainCode = mainCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDictSql() {
        return dictSql;
    }

    public void setDictSql(String dictSql) {
        this.dictSql = dictSql;
    }

    public String getKeyVal() {
        return keyVal;
    }

    public void setKeyVal(String keyVal) {
        this.keyVal = keyVal;
    }
}