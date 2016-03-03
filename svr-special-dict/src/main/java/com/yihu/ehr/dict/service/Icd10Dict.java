package com.yihu.ehr.dict.service;

import com.yihu.ehr.util.ObjectVersion;
import com.yihu.ehr.util.PinyinUtil;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ICD10特殊字典管理.
 *
 * @author cws
 * @version 1.0
 * @updated 02-6月-2015 20:25:02
 */
@Entity
@Table(name = "icd10_dict")
@Access(value = AccessType.PROPERTY)
public class Icd10Dict implements Serializable{

    public Icd10Dict() {
    }

    private String id;
    private String code;
    private String name;
    private String phoneticCode;
    private String chronicFlag;
    private String infectiousFlag;
    private String description;

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

    @Column(name = "code",  nullable = true)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
        this.phoneticCode = PinyinUtil.getPinYinHeadChar(name, true);
    }

    @Column(name = "phonetic_code", nullable = false)
    public String getPhoneticCode() {
        return phoneticCode;
    }
    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }

    @Column(name = "chronic_flag",  nullable = true)
    public String getChronicFlag() {
        return chronicFlag;
    }
    public void setChronicFlag(String chronicFlag) {
        this.chronicFlag = chronicFlag;
    }

    @Column(name = "infectious_flag",  nullable = true)
    public String getInfectiousFlag() {
        return infectiousFlag;
    }
    public void setInfectiousFlag(String infectiousFlag) {
        this.infectiousFlag = infectiousFlag;
    }

    @Column(name = "description",  nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}