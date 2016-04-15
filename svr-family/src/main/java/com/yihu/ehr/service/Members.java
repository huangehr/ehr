package com.yihu.ehr.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
    private int relationshipWithHouseholder;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
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

    @Column(name = "relationship_with_householder",  nullable = false)
    public int getRelationshipWithHouseholder() {
        return relationshipWithHouseholder;
    }
    public void setRelationshipWithHouseholder(int relationshipWithHouseholder) {
        this.relationshipWithHouseholder = relationshipWithHouseholder;
    }
}