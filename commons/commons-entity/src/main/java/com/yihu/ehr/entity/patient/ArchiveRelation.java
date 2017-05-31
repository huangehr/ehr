package com.yihu.ehr.entity.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 档案认领申请列表
 * Created by hzp on 2017/04/05.
 */
@Entity
@Table(name="archive_relation")
public class ArchiveRelation {

    protected Long id; // 非业务主键
    private String name;//姓名
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private String idCardNo;//身份证号码
    private String cardType;//就诊卡类型
    private String cardNo;//就诊卡号
    private String eventNo;//就诊事件号
    private Date eventDate;//就诊时间
    private String eventType;//就诊类型 0门诊 1住院 2体检
    private String profileId;//关联档案号
    private String status;//关联状态 0未关联 1已关联
    private Date relationDate;//关联时间
    private Date createDate;//创建时间
    private Long applyId;//关联档案申请id
    private Long cardId;//申领卡ID
    private String identifyFlag;//身份识别标识 0不可识别 1可以识别


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "id_card_no")
    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Column(name = "card_no")
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Column(name = "card_type")
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Column(name = "event_no")
    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    @Column(name = "event_date")
    public Date getEventDate() {
        return eventDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Column(name = "event_type")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Column(name = "profile_id")
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "relation_date")
    public Date getRelationDate() {
        return relationDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setRelationDate(Date relationDate) {
        this.relationDate = relationDate;
    }

    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "apply_id")
    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    @Column(name = "card_id")
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    @Column(name = "identify_flag")
    public String getIdentifyFlag() {
        return identifyFlag;
    }

    public void setIdentifyFlag(String identifyFlag) {
        this.identifyFlag = identifyFlag;
    }
}
