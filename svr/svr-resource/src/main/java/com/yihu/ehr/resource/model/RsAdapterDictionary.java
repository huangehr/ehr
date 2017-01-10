package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author linaz
 * @created 2016.05.30 13:48
 */
@Entity
@Table(name="rs_adapter_dictionary")
@Access(value = AccessType.PROPERTY)
public class RsAdapterDictionary {

    private String id;
    private String schemeId;
    private String dictCode;
    private String dictEntryCode;
    private String srcDictCode;
    private String srcDictEntryCode;
    private String srcDictEntryName;

    @Id
    @GeneratedValue(generator="Generator")
    @GenericGenerator(name="Generator",strategy = "assigned")
    @Column(name="id",nullable = false,unique = true)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="scheme_id",nullable = false)
    public String getSchemeId() {
        return schemeId;
    }
    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    @Column(name="dict_code",nullable = true)
    public String getDictCode() {
        return dictCode;
    }
    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    @Column(name="dict_entry_code",nullable = true)
    public String getDictEntryCode() {
        return dictEntryCode;
    }
    public void setDictEntryCode(String dictEntryCode) {
        this.dictEntryCode = dictEntryCode;
    }

    @Column(name="src_dict_code",nullable = false)
    public String getSrcDictCode() {
        return srcDictCode;
    }
    public void setSrcDictCode(String srcDictCode) {
        this.srcDictCode = srcDictCode;
    }

    @Column(name="src_dict_entry_code",nullable = false)
    public String getSrcDictEntryCode() {
        return srcDictEntryCode;
    }
    public void setSrcDictEntryCode(String srcDictEntryCode) {
        this.srcDictEntryCode = srcDictEntryCode;
    }

    @Column(name="src_dict_entry_name",nullable = true)
    public String getSrcDictEntryName() {
        return srcDictEntryName;
    }
    public void setSrcDictEntryName(String srcDictEntryName) {
        this.srcDictEntryName = srcDictEntryName;
    }
}