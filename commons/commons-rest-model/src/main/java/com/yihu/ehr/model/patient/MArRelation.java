package com.yihu.ehr.model.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
public class MArRelation {

    private int id;          //编号
    private String idCard;   //申请人身份证号
    private int arApplyId;   //档案申请编号
    private String archiveId;  //档案编号
    private String status;     //关联状态
    private Date relationDate;//关联时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getArApplyId() {
        return arApplyId;
    }

    public void setArApplyId(int arApplyId) {
        this.arApplyId = arApplyId;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getRelationDate() {
        return relationDate;
    }

    public void setRelationDate(Date relationDate) {
        this.relationDate = relationDate;
    }
}
