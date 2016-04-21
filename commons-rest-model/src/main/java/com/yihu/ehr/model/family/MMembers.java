package com.yihu.ehr.model.family;// default package

import javax.persistence.*;

/**
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
public class MMembers {

    private String id;
    private String familyId;
    private String idCardNo;
    private int familyRelation;

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

    public int getFamilyRelation() {
        return familyRelation;
    }
    public void setFamilyRelation(int familyRelation) {
        this.familyRelation = familyRelation;
    }
}