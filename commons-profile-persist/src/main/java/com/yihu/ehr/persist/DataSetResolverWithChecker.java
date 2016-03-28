package com.yihu.ehr.persist;

import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.profile.SimpleDataSetResolver;
import com.yihu.ehr.profile.StdObjectQualifierTranslator;
import com.yihu.ehr.schema.StdKeySchema;
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
    CacheReader cacheReader;

    @Autowired
    StdKeySchema keySchema;

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
    protected String[] translateMetaData(String innerVersion,
                                         String dataSetCode,
                                         String metaDataInnerCode,
                                         String actualData,
                                         boolean isOriginDataSet) {
        if (StringUtils.isEmpty(actualData)) return null;

        String typeKey = cacheReader.read(keySchema.metaDataType(innerVersion, dataSetCode, metaDataInnerCode));
        if (StringUtils.isEmpty(typeKey)) {
            String msg = "Meta data %1 of data set %2 is NOT found in version %3. Maybe you need cache standards?"
                    .replace("%1", metaDataInnerCode)
                    .replace("%2", dataSetCode)
                    .replace("%3", innerVersion);

            LogService.getLogger().error(msg);
            return null;
        }

        actualData = actualData.trim();

        // 仅对标准化数据集及有关联字典的数据元进行翻译
        long dictId = cacheReader.read(keySchema.metaDataDict(innerVersion, dataSetCode, metaDataInnerCode));
        if (!isOriginDataSet && StringUtils.isNotEmpty(actualData) && dictId > 0) {
            String[] tempQualifiers = StdObjectQualifierTranslator.splitInnerCodeAsCodeValue(metaDataInnerCode);

            String codeQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[0], typeKey);
            String valueQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[1], typeKey);

            String value = cacheReader.read(keySchema.dictEntryValue(innerVersion, Long.toString(dictId), actualData));

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            if (typeKey.startsWith("D")) {
                actualData = actualData.replace(".0", "");
            }

            return new String[]{StdObjectQualifierTranslator.toHBaseQualifier(metaDataInnerCode, typeKey), actualData};
        }
    }
}
