package com.yihu.ehr.standard.datasets.service;

import com.yihu.ehr.standard.version.service.CDAVersion;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * 数据集实现类. ID与版本联合起来表示一个主键。此数据集用于表示一个已经版本好的对象。
 *
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:03
 */
@MappedSuperclass
public class BaseDataSet {

    private long id;
    private long documentId;
    private int catalog;
    private int hashCode;
    private int lang;
    private int publisher;
    private boolean multiRecord;
    private String reference;
    private String stdVersion;
    private String code;
    private String name;
    private String summary;
    private String innerVersionId;

    public BaseDataSet(){
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "catalog", unique = false, nullable = false)
    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    @Column(name = "code", unique = false, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "document_id", unique = false, nullable = true)
    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    @Column(name = "hash", unique = false, nullable = true)
    public int getHashCode() {
        hashCode = Objects.hash(documentId, catalog, lang, reference,
                publisher, stdVersion, code, name, summary);

        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Column(name = "lang", unique = false, nullable = true)
    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    @Column(name = "name", unique = false, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "publisher", unique = false, nullable = false)
    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    @Column(name = "ref_standard", unique = false, nullable = false)
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Column(name = "std_version", unique = false, nullable = true)
    public String getStdVersion() {
        return stdVersion;
    }

    public void setStdVersion(String stdVersion) {
        this.stdVersion = stdVersion;
    }

    @Column(name = "summary", unique = false, nullable = true)
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Column(name = "multi_record", nullable = false)
    public boolean isMultiRecord() {
        return multiRecord;
    }

    public void setMultiRecord(boolean multiRecord) {
        this.multiRecord = multiRecord;
    }

    @Transient
    public void setInnerVersion(CDAVersion innerVersion) {
        this.innerVersionId = innerVersion.getVersion();
    }

    @Transient
    public String getInnerVersionId() {
        return innerVersionId;
    }

    public void setInnerVersionId(String innerVersionId) {
        this.innerVersionId = innerVersionId;
    }
}