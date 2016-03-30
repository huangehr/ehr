package com.yihu.ehr.persist;

import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.profile.DataSetResolver;
import com.yihu.ehr.profile.StdObjectQualifierTranslator;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据集解析器，带数据元翻译。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Component
public class DataSetResolverWithTranslator extends DataSetResolver {
    @Autowired
    CacheReader cacheReader;

    @Autowired
    StdKeySchema keySchema;

    /**
     * 翻译数据元。
     *
     * @param innerVersion
     * @param dataSetCode
     * @param isOriginDataSet
     * @param metaData
     * @param actualData
     * @return
     */
    protected String[] translateMetaData(String innerVersion,
                                         String dataSetCode,
                                         String metaData,
                                         String actualData,
                                         boolean isOriginDataSet) {
        if (StringUtils.isEmpty(actualData)) return null;

        String metaDataType = cacheReader.read(keySchema.metaDataType(innerVersion, dataSetCode, metaData));
        if (StringUtils.isEmpty(metaDataType)) {
            String msg = "Meta data %1 in data set %2 is not found in version %3. FORGET cache standards?"
                    .replace("%1", metaData)
                    .replace("%2", dataSetCode)
                    .replace("%3", innerVersion);

            LogService.getLogger().error(msg);
            return null;
        }

        actualData = actualData.trim();

        // only translate meta that bind with dict
        long dictId = cacheReader.read(keySchema.metaDataDict(innerVersion, dataSetCode, metaData));
        if (!isOriginDataSet && StringUtils.isNotEmpty(actualData) && dictId > 0) {
            String[] tempQualifiers = StdObjectQualifierTranslator.splitMetaData(metaData);

            String codeQualifier = StdObjectQualifierTranslator.hBaseQualifier(tempQualifiers[0], metaDataType);
            String valueQualifier = StdObjectQualifierTranslator.hBaseQualifier(tempQualifiers[1], metaDataType);

            String value = cacheReader.read(keySchema.dictEntryValue(innerVersion, Long.toString(dictId), actualData));

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            // date value looks dirty sometimes
            if (metaDataType.startsWith("D")) {
                actualData = actualData.replace(".0", "");
            }

            return new String[]{StdObjectQualifierTranslator.hBaseQualifier(metaData, metaDataType), actualData};
        }
    }
}
