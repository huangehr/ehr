package com.yihu.ehr.entity.tj;


import javax.persistence.*;
import java.io.Serializable;

/**
 * 统计主纬度关联表
 *
 * @author janseny
 * @version 1.0
 * @updated 2017年6月8日
 */
@Entity
@Table(name = "tj_quota_dimension_main")
@Access(value = AccessType.PROPERTY)
public class TjQuotaDimensionMain implements Serializable{


    private Long id;
    private String quotaCode;  //关联 tj_quota code
    private String mainCode; //关联 jt_dimension_main
    private String dictSql;//
    private String keyVal;//

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "quota_code",  nullable = true)
    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    @Column(name = "main_code",  nullable = true)
    public String getMainCode() {
        return mainCode;
    }

    public void setMainCode(String mainCode) {
        this.mainCode = mainCode;
    }

    @Column(name = "dict_sql",  nullable = true)
    public String getDictSql() {
        return dictSql;
    }

    public void setDictSql(String dictSql) {
        this.dictSql = dictSql;
    }

    @Column(name = "key_val",  nullable = true)
    public String getKeyVal() {
        return keyVal;
    }

    public void setKeyVal(String keyVal) {
        this.keyVal = keyVal;
    }
}