package com.yihu.quota.vo;

/**
 * Created by chenweida on 2017/6/1.
 */
public class ErrModel {
    private String businessId;
    private String message;

    public ErrModel(String businessId, String message) {
        this.businessId = businessId;
        this.message = message;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
