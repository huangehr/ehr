package com.yihu.ehr.entity.quota;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/9.
 */
@Entity
@Table(name = "tj_quota_data_source", schema = "", catalog = "healtharchive")
public class TjQuotaDataSource implements Serializable{
    private Long id;
    private String quotaCode;
    private String sourceCode;
    private String configJson;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "quota_code")
    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    @Column(name = "source_code")
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Column(name = "config_json")
    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
}
