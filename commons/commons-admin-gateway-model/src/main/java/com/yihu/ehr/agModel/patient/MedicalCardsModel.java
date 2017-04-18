package com.yihu.ehr.agModel.patient;

import java.util.Date;

/**
 * Created by janseny on 2017/4/17.
 */
public class MedicalCardsModel {

    private Long id;
    private String cardType;//'卡类别【字典】',
    private String cardTypeName;
    private String cardNo;//'就诊卡号',
    private String releaseOrg;//发卡机构',
    private String releaseDate;//发卡时间',
    private String validityDateBegin;//'有效期起始时间',
    private String validityDateEnd;//有效期截止时间',
    private String description;//描述',
    private String status;//卡状态 0无效 1有效',
    private String createDate;//'创建时间',
    private String creater;//创建者',
    private String updateDate;//修改时间',
    private String updater;//修改者'

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getValidityDateBegin() {
        return validityDateBegin;
    }

    public void setValidityDateBegin(String validityDateBegin) {
        this.validityDateBegin = validityDateBegin;
    }

    public String getValidityDateEnd() {
        return validityDateEnd;
    }

    public void setValidityDateEnd(String validityDateEnd) {
        this.validityDateEnd = validityDateEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}
