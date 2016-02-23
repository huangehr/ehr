package com.yihu.ehr.util;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class CDAVersionUtil {
    public final static String DataSetTablePrefix = "std_data_set_";
    public final static String MetaDataTablePrefix = "std_meta_data_";
    public final static String DictTablePrefix = "std_dictionary_";
    public final static String DictEntryTablePrefix = "std_dictionary_entry_";
    public final static String CDADocumentTablePrefix = "std_cda_document_";
    public final static String CDADatasetRelationshipTablePrefix = "std_cda_data_set_relationship_";


    public static String getDataSetTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return DataSetTablePrefix + version;
    }

    public static String getMetaDataTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return MetaDataTablePrefix + version;
    }

    public static String getDictTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return DictTablePrefix + version;
    }

    public static String getDictEntryTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return DictEntryTablePrefix + version;
    }

    public static String getCDATableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return CDADocumentTablePrefix + version;
    }

    public static String getCDADatasetRelationshipTableName(String version) {
        if (!ObjectVersion.isValid(version)) throw new IllegalArgumentException("无效版本号");

        return CDADatasetRelationshipTablePrefix + version;
    }

}