package com.yihu.ehr.persist;

import com.yihu.ehr.profile.SimpleDataSetResolver;
import com.yihu.ehr.schema.StdObjectQualifierTranslator;
import com.yihu.ehr.schema.StdRedisCacheAccessor;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    StdRedisCacheAccessor redisCache;

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
        if (StringUtils.isEmpty(actualData)) return null;

        String type = redisCache.getMetaDataType(innerVersion, dataSetCode, metaDataInnerCode);
        if (StringUtils.isEmpty(type)) {
            String msg = "Meta data %1 of data set %2 is NOT found in version %3. Please check the meta data."
                    .replace("%1", metaDataInnerCode)
                    .replace("%2", dataSetCode)
                    .replace("%3", innerVersion);

            LogService.getLogger().warn(msg);
            return null;
        }

        actualData = actualData.trim();

        // 仅对标准化数据集及有关联字典的数据元进行翻译
        long dictId = redisCache.getMetaDataDict(innerVersion, dataSetCode, metaDataInnerCode);
        if (!isOriginDataSet && StringUtils.isNotEmpty(actualData) && dictId > 0) {
            String[] tempQualifiers = StdObjectQualifierTranslator.splitInnerCodeAsCodeValue(metaDataInnerCode);

            String codeQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[0], type);
            String valueQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[1], type);

            String value = redisCache.getDictEntryValue(innerVersion, dictId, actualData);

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            if (type.equals("D")) {
                actualData = actualData.length() <= 10 ? actualData : actualData.substring(0, actualData.lastIndexOf(' ')) + " 00:00:00";
            } else if (type.equals("DT")) {
                actualData = actualData.contains(".") ? actualData.substring(0, actualData.lastIndexOf('.')) : actualData;
            } else if (type.equals("N")) {
                Matcher matcher = NumberPattern.matcher(actualData);
                if (matcher.find()) {
                    actualData = matcher.group();
                } else {
                    actualData = "";
                }
            }

            return new String[]{StdObjectQualifierTranslator.toHBaseQualifier(metaDataInnerCode, type), actualData};
        }
    }
}
