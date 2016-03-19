package com.yihu.ehr.persist;

import com.yihu.ehr.cache.CachedMetaData;
import com.yihu.ehr.cache.StdDataRedisCache;
import com.yihu.ehr.cache.StdObjectQualifierTranslator;
import com.yihu.ehr.profile.SimpleDataSetResolver;
import com.yihu.ehr.util.log.LogService;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

/**
 * 数据集解析器，带数据元验证。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Component
public class DataSetResolverWithChecker extends SimpleDataSetResolver {
    /**
     * 翻译数据元。
     *
     * @param innerVersion
     * @param dataSetCode
     * @param isOriginDataSet
     * @param metaDataInnerCode
     * @param actualData
     * @return
     */
    protected String[] standardizeMetaData(String innerVersion,
                                           String dataSetCode,
                                           String metaDataInnerCode,
                                           String actualData,
                                           boolean isOriginDataSet) {
        actualData = (actualData == null) ? "" : actualData.trim();

        CachedMetaData metaData = StdDataRedisCache.getMetaData(innerVersion, dataSetCode, metaDataInnerCode);
        if (null == metaData) {
            String msg = "Meta data %1 of data set %2 is NOT found in version %3. Please check the meta data."
                    .replace("%1", metaDataInnerCode)
                    .replace("%2", dataSetCode)
                    .replace("%3", innerVersion);

            LogService.getLogger().warn(msg);
            return null;
        }

        // 仅对标准化数据集及有关联字典的数据元进行翻译
        if (!isOriginDataSet && actualData != null && actualData.length() > 0 && metaData.dictId > 0) {
            String[] tempQualifiers = StdObjectQualifierTranslator.splitInnerCodeAsCodeValue(metaDataInnerCode);

            String codeQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[0], metaData.type);
            String valueQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[1], metaData.type);

            String value = StdDataRedisCache.getDictEntryValue(innerVersion, metaData.dictId, actualData);

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            if (metaData.type.equals("D")) {
                actualData = actualData.length() <= 10 ? actualData : actualData.substring(0, actualData.lastIndexOf(' ')) + " 00:00:00";
            } else if (metaData.type.equals("DT")) {
                actualData = actualData.contains(".") ? actualData.substring(0, actualData.lastIndexOf('.')) : actualData;
            } else if (metaData.type.equals("N")) {
                Matcher matcher = NumberPattern.matcher(actualData);
                if (matcher.find()) {
                    actualData = matcher.group();
                } else {
                    actualData = "";
                }
            }

            return new String[]{StdObjectQualifierTranslator.toHBaseQualifier(metaDataInnerCode, metaData.type), actualData};
        }
    }
}
