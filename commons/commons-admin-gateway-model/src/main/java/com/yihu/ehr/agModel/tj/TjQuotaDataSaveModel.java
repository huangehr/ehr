package com.yihu.ehr.agModel.tj;

/**
 * Created by Administrator on 2017/6/13.
 */
public class TjQuotaDataSaveModel {
    private Long id;
    private String quotaCode;
    private String saveCode;
    private String configJson;

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
}
