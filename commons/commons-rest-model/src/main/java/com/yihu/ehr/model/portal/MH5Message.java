package com.yihu.ehr.model.portal;

import java.util.Map;

/**
 * 医疗云-APP预约挂号订单推送接入
 *
 * @author zdm
 * @since 2018.04.10
 */
public class MH5Message {
    /**
     * 定值-（yihuwang）医护网
     */
    private String agencyAbb;
    /**
     * 根据分配给第三方的秘钥对参数进行的签名，第三方可以根据此签名校验（避免非法调用）
     */
    private String sign;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 渠道号
     */
    private String appId;
    /**
     * 健康之路订单号
     */
    private String orderId;
    /**
     * 推送类型：101：挂号结果推送，102：退号结果推送，-101：订单操作推送
     */
    private int type;
    /**
     * 是否成功，0：成功，1：失败
     */
    private int isSuccess;
    /**
     * 第三方订单ID
     */
    private String thirdPartyOrderId;
    /**
     * 消息Str
     */
    private Map data;
    /**
     * 健康之路用户Id
     */
    private String userId;
    /**
     * 第三方用户Id
     */
    private String thirdPartyUserId;

    public String getAgencyAbb() {
        return agencyAbb;
    }

    public void setAgencyAbb(String agencyAbb) {
        this.agencyAbb = agencyAbb;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getThirdPartyOrderId() {
        return thirdPartyOrderId;
    }

    public void setThirdPartyOrderId(String thirdPartyOrderId) {
        this.thirdPartyOrderId = thirdPartyOrderId;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThirdPartyUserId() {
        return thirdPartyUserId;
    }

    public void setThirdPartyUserId(String thirdPartyUserId) {
        this.thirdPartyUserId = thirdPartyUserId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }
}
