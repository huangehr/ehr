package com.yihu.ehr.agModel.patient;

/**
 * Created by AndyCai on 2016/2/25.
 */
public class CardModel {
    String id;				    // 卡ID
    String number;				// 卡号
    String ownerName;			// 持有人姓名
    String status;			// 状态 CardStatus
    String statusName;
    String cardType;				// 类型 CardType
    String typeName;
    String createDate;				// 创建日期
    String releaseOrg;
    String releaseOrgName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg;
    }

    public String getReleaseOrgName() {
        return releaseOrgName;
    }

    public void setReleaseOrgName(String releaseOrgName) {
        this.releaseOrgName = releaseOrgName;
    }
}
