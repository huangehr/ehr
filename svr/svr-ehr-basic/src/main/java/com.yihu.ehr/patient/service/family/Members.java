package com.yihu.ehr.patient.service.family;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
@Entity
@Table(name = "members")
@Access(value = AccessType.PROPERTY)
public class Members {

    private String id;
    private String familyId;
    private String idCardNo;
    private String familyRelation;
    private Date createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "family_id",  nullable = false)
    public String getFamilyId() {
        return familyId;
    }
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    @Column(name = "id_card_no",  nullable = false)
    public String getIdCardNo() {
        return idCardNo;
    }
    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Column(name = "family_relation",  nullable = false)
    public String getFamilyRelation() {
        return familyRelation;
    }
    public void setFamilyRelation(String familyRelation) {
        this.familyRelation = familyRelation;
    }

    @Column(name="create_date",nullable = true)
    public Date getCreateDate(){
        return createDate;
    }
    public void setCreateDate(Date createDate){
        this.createDate = createDate;
    }

}