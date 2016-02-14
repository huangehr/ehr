//package com.yihu.ehr.standard.cdaversion.service;
//
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.yihu.ehr.util.ObjectVersion;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//import java.util.Date;
//
///**
// * @author Sand
// * @version 1.0
// * @created 02-7月-2015 14:36:32
// */
////@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@Entity
//@Table(name = "std_cda_versions")
//@Access(value = AccessType.PROPERTY)
//public class CDAVersion{
//    public final static String DataSetTablePrefix = "std_data_set_";
//    public final static String MetaDataTablePrefix = "std_meta_data_";
//    public final static String DictTablePrefix = "std_dictionary_";
//    public final static String DictEntryTablePrefix = "std_dictionary_entry_";
//    public final static String CDADocumentTablePrefix = "std_cda_document_";
//    public final static String CDADatasetRelationshipTablePrefix = "std_cda_data_set_relationship_";
//
//    private String author;
//    private Date commitTime;
//    private boolean isInStage;
//    private ObjectVersion version;
//    private String versionName;
//    private ObjectVersion baseVersion;
//
//    public CDAVersion() {
//    }
//
//    public CDAVersion(String baseVersion, String author, String versionName) {
//        //this.baseVersion = baseVersion == null ? null : new ObjectVersion(baseVersion);
//        this.author = author;
//        this.commitTime = null;
//        this.isInStage = true;
//        this.version = new ObjectVersion();
//        if (baseVersion == null) {
//            this.versionName = versionName;
//            this.baseVersion = null;
//        } else {
//            versionName = versionName.substring(1, versionName.length());
//            double dVersion = Double.parseDouble(versionName) + 1.0;
//            this.versionName = "V" + String.valueOf(dVersion);
//
//            this.baseVersion = new ObjectVersion(baseVersion);
//        }
//    }
//
//    public void finalize() throws Throwable {
//
//    }
//
//    @Id
//    @GeneratedValue(generator = "Generator")
//    @GenericGenerator(name = "Generator", strategy = "assigned")
//    @Column(name = "version", unique = true, nullable = false)
//    @Access(value = AccessType.PROPERTY)
//    public String getVersion() {
//        if(version==null)return "";
//        return version.toString();
//    }
//    public void setVersion(String version) {
//        this.version = new ObjectVersion(version);
//    }
//
//    @Column(name = "author", unique = false, nullable = false)
//    public String getAuthor() {
//        return author;
//    }
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    @Column(name = "base_version", unique = false, nullable = true)
//    @Access(value = AccessType.PROPERTY)
//    public String getBaseVersion() {
//        if (baseVersion == null)
//            return null;
//        return baseVersion.toString();
//    }
//    public void setBaseVersion(String baseVersion) {
//        this.baseVersion = baseVersion == null ? null : new ObjectVersion(baseVersion);
//    }
//
//    @Column(name = "commit_time", unique = false, nullable = true)
//    public Date getCommitTime() {
//        return commitTime;
//    }
//    public void setCommitTime(Date commitTime) {
//        this.commitTime = commitTime;
//    }
//
//    @Column(name = "version_name", unique = false, nullable = true)
//    public String getVersionName() {
//        return versionName;
//    }
//    public void setVersionName(String versionName) {
//        this.versionName = versionName;
//    }
//
//    @Column(name = "staged", unique = false, nullable = true )
//    public boolean isInStage() {
//        return isInStage;
//    }
//    public void setInStage(boolean isInStage) {
//        this.isInStage = isInStage;
//    }
//
//
//    @Transient
//    public String getDataSetTableName() {
//        return getDataSetTableName(version.toString());
//    }
//    @Transient
//    public static String getDataSetTableName(String version) {
//        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");
//
//        return DataSetTablePrefix + version;
//    }
//    @Transient
//    public String getMetaDataTableName() {
//        return getMetaDataTableName(version.toString());
//    }
//    @Transient
//    public static String getMetaDataTableName(String version) {
//        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");
//
//        return MetaDataTablePrefix + version;
//    }
//
//    //获取字典表名称
//    @Transient
//    public String getDictTableName() {
//        return getDictTableName(version.toString());
//    }
//    @Transient
//    public static String getDictTableName(String version) {
//        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");
//
//        return DictTablePrefix + version;
//    }
//
//    //获取字典项表名称
//    @Transient
//    public String getDictEntryTableName() {
//        return getDictEntryTableName(version.toString());
//    }
//    @Transient
//    public static String getDictEntryTableName(String version) {
//        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");
//
//        return DictEntryTablePrefix + version;
//    }
//    @Transient
//    public String getCDADocumentTableName() {
//        return getCDATableName(version.toString());
//    }
//    @Transient
//    public static String getCDATableName(String version) {
//        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");
//
//        return CDADocumentTablePrefix + version;
//    }
//    @Transient
//    public String getCDADatasetRelationshipTableName() {
//        return getCDADatasetRelationshipTableName(version.toString());
//    }
//    @Transient
//    public static String getCDADatasetRelationshipTableName(String version) {
//        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");
//
//        return CDADatasetRelationshipTablePrefix + version;
//    }
//
//    @Transient
//    public String getBaseDataSetTableName() {
//        return getDataSetTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
//    }
//    @Transient
//    public String getBaseMetaDataTableName() {
//        return getMetaDataTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
//    }
//    @Transient
//    public String getBaseDictTableName() {
//        return getDictTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
//    }
//    @Transient
//    public String getBaseDictEntryTableName() {
//        return getDictEntryTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
//    }
//    @Transient
//    public String getBaseCDADocumentTableName() {
//        return getCDATableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
//    }
//    @Transient
//    public String getBaseCDADatasetRelationshipTableName() {
//        return getCDADatasetRelationshipTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
//    }
//}