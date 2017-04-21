package com.yihu.ehr.model.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.model.common.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * �û�������
 * Created by cws on 2017/04/14.
 */
@Entity
@Table(name="medical_cards")
public class MedicalCards extends IdEntity {

    private String cardType;//'卡类别【字典】',
    private String cardNo;//'就诊卡号',
    private String releaseOrg;//发卡机构',
    private Date releaseDate;//发卡时间',
    private Date validityDateBegin;//'有效期起始时间',
    private Date validityDateEnd;//有效期截止时间',
    private String description;//描述',
    private String status;//卡状态 0无效 1有效',
    private Date createDate;//'创建时间',
    private String creater;//创建者',
    private Date updateDate;//修改时间',
    private String updater;//修改者'

    @Column(name = "card_type")
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Column(name = "card_no")
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Column(name = "release_org")
    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg;
    }

    @Column(name = "release_date")
    public Date getReleaseDate() {
        return releaseDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Column(name = "validity_date_begin")
    public Date getValidityDateBegin() {
        return validityDateBegin;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setValidityDateBegin(Date validityDateBegin) {
        this.validityDateBegin = validityDateBegin;
    }

    @Column(name = "validity_date_end")
    public Date getValidityDateEnd() {
        return validityDateEnd;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setValidityDateEnd(Date validityDateEnd) {
        this.validityDateEnd = validityDateEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    @Column(name = "update_date")
    public Date getUpdateDate() {
        return updateDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}
