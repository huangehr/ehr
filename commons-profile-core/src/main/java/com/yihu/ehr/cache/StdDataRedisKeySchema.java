package com.yihu.ehr.cache;

import com.yihu.ehr.util.StringBuilderUtil;

/**
 * Redis 缓存的标准数据策略生成器.
 *
 * <p>
 * 数据元的缓存格式: innerVersion.dataSetCode.metaDataInnerCode : CachedMetaData
 * 字典项的缓存格式: innerVersion.dictId.entryCode : Entry String Value
 * <p>
 *
 * 其中, innerVersion 作为Redis缓存的前缀.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.01 19:01
 */
public class StdDataRedisKeySchema {
    /**
     * 数据集Key。
     *
     * @param innerVersion
     * @param code
     * @return
     */
    public static String makeDataSetKey(String innerVersion, String code){
        return new StringBuilderUtil("%1.%2").arg(innerVersion).arg(code).toString();
    }

    /**
     * 数据元的Key.
     *
     * @param innerVersion
     * @param dataSetCode
     * @param innerCode
     * @return
     */
    public static String makeMetaDataKey(String innerVersion, String dataSetCode, String innerCode) {
        assert innerVersion != null && innerVersion.length() != 0 &&
                dataSetCode != null && dataSetCode.length() != 0 &&
                innerCode != null && innerCode.length() != 0;

        return new StringBuilderUtil("%1.%2.%3").arg(innerVersion).arg(dataSetCode).arg(innerCode).toString();
    }

    /**
     * 字典项Key.
     *
     * @param innerVersion
     * @param dictId
     * @param entryCode
     * @return
     */
    public static String makeDictEntryKey(String innerVersion, long dictId, String entryCode) {
        assert innerVersion != null && innerVersion.length() > 0 &&
                dictId > 0 &&
                entryCode != null && entryCode.length() != 0;

        return new StringBuilderUtil("%1.%2.%3").arg(innerVersion).arg(dictId).arg(entryCode).toString();
    }
}
