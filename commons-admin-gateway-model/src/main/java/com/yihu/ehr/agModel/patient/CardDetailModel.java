package com.yihu.ehr.agModel.patient;

import java.util.Date;

/**
 * Created by AndyCai on 2016/2/25.
 */
public class CardDetailModel {
    String id;				    // 卡ID
    String number;				// 卡号
    String ownerName;			// 持有人姓名
    String status;			// 状态 CardStatus
    String statusName;
    String cardType;				// 类型 CardType
    String typeName;
    String description;			// 描述
    Date createDate;				// 创建日期
    String idCardNo;
    String DType;
    //特殊字段
    String local;				// 发行地/归属地
    String releaseOrg;				// 发行机构
    String releaseOrgName;
    Date releaseDate;			// 发行时间
    Date validityDateBegin;		// 有效期起始时间
    Date validityDateEnd;		// 有效期结束时间

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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getDType() {
        return DType;
    }

    public void setDType(String DType) {
        this.DType = DType;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getValidityDateBegin() {
        return validityDateBegin;
    }

    public void setValidityDateBegin(Date validityDateBegin) {
        this.validityDateBegin = validityDateBegin;
    }

    public Date getValidityDateEnd() {
        return validityDateEnd;
    }

    public void setValidityDateEnd(Date validityDateEnd) {
        this.validityDateEnd = validityDateEnd;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getReleaseOrgName() {
        return releaseOrgName;
    }

    public void setReleaseOrgName(String releaseOrgName) {
        this.releaseOrgName = releaseOrgName;
    }
}
