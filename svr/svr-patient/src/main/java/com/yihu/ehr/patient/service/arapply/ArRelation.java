package com.yihu.ehr.patient.service.arapply;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@Entity
@Table(name = "archive_apply_relation")
public class ArRelation {

    private int id;          //编号
    private String idCard;   //申请人身份证号
    private int arApplyId;   //档案申请编号
    private String archiveId;  //档案编号
    private String status;     //关联状态
    private Date relationDate;//关联时间

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "id_card", nullable = false)
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Column(name = "archive_apply_id", nullable = false)
    public int getArApplyId() {
        return arApplyId;
    }

    public void setArApplyId(int arApplyId) {
        this.arApplyId = arApplyId;
    }

    @Column(name = "archive_id", nullable = false)
    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "relation_date", nullable = true)
    public Date getRelationDate() {
        return relationDate;
    }

    public void setRelationDate(Date relationDate) {
        this.relationDate = relationDate;
    }
}
