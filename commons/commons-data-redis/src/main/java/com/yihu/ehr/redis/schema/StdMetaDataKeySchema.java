package com.yihu.ehr.redis.schema;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Created by hzp on 2017.04.25
 */
@Component
public class StdMetaDataKeySchema extends KeySchema {
    private String MetaDataTable = "std_meta_data_";

    private String MetaDataDictIdColumn = "dict_id";

    private String MetaDataTypeColumn = "type";
    private String MetaDataFormatColumn = "format";
    private String MetaDataNullableColumn = "nullable";

    private String DictEntryTable = "std_dictionary_entry_";

    private String DictEntryValueColumn = "value";
    private String DictEntryCodeColumn = "code";

    public String metaDataDict(String version, String dataSetCode, String innerCode) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return redisClient.get(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataDictIdColumn));
    }

    public void setMetaDataDict(String version, String dataSetCode, String innerCode, String value) {
        redisClient.set(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataDictIdColumn), value);
    }

    public void deleteMetaDataDict(String version, String dataSetCode, String innerCode) {
        redisClient.delete(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataDictIdColumn));
    }


    public String metaDataType(String version, String dataSetCode, String innerCode) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return redisClient.get(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataTypeColumn));
    }


    public void setMetaDataType(String version, String dataSetCode, String innerCode, String value) {
        redisClient.set(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataTypeColumn), value);
    }

    public void deleteMetaDataType(String version, String dataSetCode, String innerCode) {
        redisClient.delete(makeKey(MetaDataTable + version, dataSetCode + "." + innerCode, MetaDataTypeColumn));
    }

    public String metaDataFormat(String version, String dataSetCode, String code) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                code != null && code.length() != 0;

        return redisClient.get(makeKey(MetaDataTable + version, dataSetCode + "." + code, MetaDataFormatColumn));
    }

    public void setMetaDataFormat(String version, String dataSetCode, String code, String value) {
        redisClient.set(makeKey(MetaDataTable + version, dataSetCode + "." + code, MetaDataFormatColumn), value);
    }

    public void deleteMetaDataFormat(String version, String dataSetCode, String code) {
        redisClient.delete(makeKey(MetaDataTable + version, dataSetCode + "." + code, MetaDataFormatColumn));
    }

    public Boolean metaDataNullable(String version, String dataSetCode, String code) {
        assert version != null && version.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                code != null && code.length() != 0;

        String nullable = redisClient.get(makeKey(MetaDataTable + version, dataSetCode + "." + code, MetaDataNullableColumn));
        return nullable.equals("1");
    }

    public void setMetaDataNullable(String version, String dataSetCode, String code, String value) {
        redisClient.set(makeKey(MetaDataTable + version, dataSetCode + "." + code, MetaDataNullableColumn), value);
    }

    public void deleteMetaDataNullable(String version, String dataSetCode, String code) {
        redisClient.delete(makeKey(MetaDataTable + version, dataSetCode + "." + code, MetaDataNullableColumn));
    }

    public String dictEntryValue(String version, String dictId, String entryCode) {
        assert StringUtils.isNotBlank(version) && StringUtils.isNotBlank(entryCode);

        return redisClient.get(makeKey(DictEntryTable + version, dictId + "." + entryCode, DictEntryValueColumn));
    }

    public void setDictEntryValue(String version, String dictId, String entryCode, String value) {
        redisClient.set(makeKey(DictEntryTable + version, dictId + "." + entryCode, DictEntryValueColumn), value);
    }

    public void deleteDictEntryValue(String version, String dictId, String entryCode) {
        redisClient.delete(makeKey(DictEntryTable + version, dictId + "." + entryCode, DictEntryValueColumn));
    }

    public String dictEntryCode(String version, String dictId, String entryValue) {
        assert StringUtils.isNotBlank(version) && StringUtils.isNotBlank(entryValue);

        return redisClient.get(makeKey(DictEntryTable + version, dictId + "." + entryValue, DictEntryCodeColumn));
    }

    public void setDictEntryCode(String version, String dictId, String entryValue, String value) {
        redisClient.set(makeKey(DictEntryTable + version, dictId + "." + entryValue, DictEntryCodeColumn), value);
    }

    public void deleteDictEntryCode(String version, String dictId, String entryValue) {
        redisClient.delete(makeKey(DictEntryTable + version, dictId + "." + entryValue, DictEntryCodeColumn));
    }

    public Boolean isDictCodeExist(String version, String dictId, String entryCode) {
        assert StringUtils.isNotBlank(version) && StringUtils.isNotBlank(entryCode);

        return redisClient.hasKey(makeKey(DictEntryTable + version, dictId + "." + entryCode, DictEntryCodeColumn));
    }

    public Boolean isDictValueExist(String version, String dictId, String entryValue) {
        assert StringUtils.isNotBlank(version) && StringUtils.isNotBlank(entryValue);

        return redisClient.hasKey(makeKey(DictEntryTable + version, dictId + "." + entryValue, DictEntryValueColumn));
    }
}
