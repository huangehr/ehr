package com.yihu.ehr.entity.quota;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/9.
 */
@Entity
@Table(name = "tj_quota_data_save", schema = "", catalog = "healtharchive")
public class TjQuotaDataSave implements Serializable{
    private Long id;
    private String quotaCode;
    private String saveCode;
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

    @Column(name = "save_code")
    public String getSaveCode() {
        return saveCode;
    }

    public void setSaveCode(String saveCode) {
        this.saveCode = saveCode;
    }

    @Column(name = "config_json")
    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
}
