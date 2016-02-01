package com.yihu.ehr.patient.service.card;

import java.util.Date;

/**
 * SNS平台上的账户.
 *
 * @author Sand
 * @version 1.0
 * @updated 10-6月-2015 15:00:00
 */
public class SNSAccount extends AbstractVirtualCard {
    private SNSPlatform platform;      // SNS 平台

    public SNSAccount() {
        super();
    }

    public SNSAccount(String type){
        super();
        this.type = type;
    }

    public SNSAccount(String type, String number, String cardStatus, String description){
        this.number = number;
        this.status = cardStatus;
        this.description = description;
        this.createDate = new Date();
        this.type = type;
    }

    public SNSPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(SNSPlatform platform){
        this.platform = platform;
    }

    public void setCardType(String type){
        this.type = type;
    }
}
