package com.yihu.ehr.std.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yihu.ehr.util.ObjectVersion;

import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 02-7月-2015 14:36:32
 */
public class CDAVersion {
    public final static String DataSetTablePrefix = "std_data_set_";
    public final static String MetaDataTablePrefix = "std_meta_data_";
    public final static String DictTablePrefix = "std_dictionary_";
    public final static String DictEntryTablePrefix = "std_dictionary_entry_";
    public final static String CDADocumentTablePrefix = "std_cda_document_";
    public final static String CDADatasetRelationshipTablePrefix = "std_cda_data_set_relationship_";

    private String author;
    private Date commitTime;
    private boolean isInStage;
    private ObjectVersion version;
    private String versionName;
    private ObjectVersion baseVersion;

    public CDAVersion() {
    }

    public CDAVersion(String baseVersion, String author, String versionName) {
        //this.baseVersion = baseVersion == null ? null : new ObjectVersion(baseVersion);
        this.author = author;
        this.commitTime = null;
        this.isInStage = true;
        this.version = new ObjectVersion();
        if (baseVersion == null) {
            this.versionName = versionName;
            this.baseVersion = null;
        } else {
            versionName = versionName.substring(1, versionName.length());
            double d_version = Double.parseDouble(versionName) + 1.0;
            this.versionName = "V" + String.valueOf(d_version);

            this.baseVersion = new ObjectVersion(baseVersion);
        }
    }

    public void finalize() throws Throwable {
    }

    public String getAuthor() {
        return author;
    }

    public String getBaseVersion() {
        if (baseVersion == null) return null;

        return baseVersion.toString();
    }

    public String getBaseDataSetTableName() {
        return getDataSetTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
    }

    public String getBaseMetaDataTableName() {
        return getMetaDataTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
    }

    public String getBaseDictTableName() {
        return getDictTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
    }

    public String getBaseDictEntryTableName() {
        return getDictEntryTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
    }

    public String getBaseCDADocumentTableName() {
        return getCDATableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
    }

    public String getBaseCDADatasetRelationshipTableName() {
        return getCDADatasetRelationshipTableName(baseVersion == null ? new ObjectVersion(CDAVersionManager.FBVersion).toString() : baseVersion.toString());
    }


    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    public String getDataSetTableName() {
        return getDataSetTableName(version.toString());
    }

    public static String getDataSetTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return DataSetTablePrefix + version;
    }

    public String getMetaDataTableName() {
        return getMetaDataTableName(version.toString());
    }

    public static String getMetaDataTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return MetaDataTablePrefix + version;
    }

    //获取字典表名称
    public String getDictTableName() {
        return getDictTableName(version.toString());
    }

    public static String getDictTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return DictTablePrefix + version;
    }

    //获取字典项表名称
    public String getDictEntryTableName() {
        return getDictEntryTableName(version.toString());
    }

    public static String getDictEntryTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return DictEntryTablePrefix + version;
    }

    public String getCDADocumentTableName() {
        return getCDATableName(version.toString());
    }

    public static String getCDATableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return CDADocumentTablePrefix + version;
    }

    public String getCDADatasetRelationshipTableName() {
        return getCDADatasetRelationshipTableName(version.toString());
    }

    public static String getCDADatasetRelationshipTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return CDADatasetRelationshipTablePrefix + version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersion() {
        return version.toString();
    }

    public void setVersion(String version) {
        this.version = new ObjectVersion(version);
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion == null ? null : new ObjectVersion(baseVersion);
    }

    public boolean isInStage() {
        return isInStage;
    }

    public void setInStage(boolean isInStage) {
        this.isInStage = isInStage;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}