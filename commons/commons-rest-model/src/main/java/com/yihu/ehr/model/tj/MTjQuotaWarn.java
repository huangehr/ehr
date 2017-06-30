package com.yihu.ehr.model.tj;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/12.
 */
public class MTjQuotaWarn {
    private Long id;
    private String quotaCode;
    private String quotaName;
    private String value;
    private String userId;
    private String createTime;
    private String updateTime;
    private int quotaCount;
    private int status; //0 未超过预警 1 超过预警

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

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getQuotaCount() {
        return quotaCount;
    }

    public void setQuotaCount(int quotaCount) {
        this.quotaCount = quotaCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
