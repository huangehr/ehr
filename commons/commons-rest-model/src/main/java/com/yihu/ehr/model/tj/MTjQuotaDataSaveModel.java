package com.yihu.ehr.model.tj;

/**
 * Created by Administrator on 2017/6/12.
 */
public class MTjQuotaDataSaveModel {
    private Long id;
    private String quotaCode;
    private String saveCode;
    private String configJson;
    private String name;

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

    public String getSaveCode() {
        return saveCode;
    }

    public void setSaveCode(String saveCode) {
        this.saveCode = saveCode;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
