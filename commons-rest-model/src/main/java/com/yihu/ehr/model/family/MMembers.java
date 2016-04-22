package com.yihu.ehr.model.family;// default package

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
public class MMembers {

    private String id;
    private String familyId;
    private String idCardNo;
    private String familyRelation;
    private Date createDate;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFamilyId() {
        return familyId;
    }
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getIdCardNo() {
        return idCardNo;
    }
    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getFamilyRelation() {
        return familyRelation;
    }
    public void setFamilyRelation(String familyRelation) {
        this.familyRelation = familyRelation;
    }

    public Date getCreateDate(){
        return createDate;
    }
    public void setCreateDate(Date createDate){
        this.createDate = createDate;
    }
}