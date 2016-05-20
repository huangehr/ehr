package com.yihu.ehr.service.resource.stage1;

import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.schema.StdMetaDataKeySchema;
import com.yihu.ehr.service.util.DataSetResolver;
import com.yihu.ehr.service.util.QualifierTranslator;
import com.yihu.ehr.schema.StdDataSetKeySchema;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
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
    StdDataSetKeySchema dataSetKeySchema;

    @Autowired
    StdMetaDataKeySchema metaDataKeySchema;

    /**
     * 翻译数据元。
     *
     * 资源化入库时，取消文件解析时的数据元翻译动作
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param isOriginDataSet
     * @param metaData
     * @param actualData
     * @return
     */
    protected String[] translateMetaData(String cdaVersion,
                                         String dataSetCode,
                                         String metaData,
                                         String actualData,
                                         boolean isOriginDataSet) {
        if (StringUtils.isEmpty(actualData)) return null;

        String metaDataType = cacheReader.read(metaDataKeySchema.metaDataType(cdaVersion, dataSetCode, metaData));
        if (StringUtils.isEmpty(metaDataType)) {
            String msg = "Meta data %1 in data set %2 is not found in version %3. FORGET cache standards?"
                    .replace("%1", metaData)
                    .replace("%2", dataSetCode)
                    .replace("%3", cdaVersion);

            LogService.getLogger().error(msg);
            return null;
        }

        actualData = actualData.trim();

        // only translate meta that bind with dict
        long dictId = cacheReader.read(metaDataKeySchema.metaDataDict(cdaVersion, dataSetCode, metaData));
        if (!isOriginDataSet && StringUtils.isNotEmpty(actualData) && dictId > 0) {
            String[] tempQualifiers = QualifierTranslator.splitMetaData(metaData);

            String codeQualifier = tempQualifiers[0];   /*QualifierTranslator.hBaseQualifier(tempQualifiers[0], metaDataType);*/
            String valueQualifier = tempQualifiers[1];  /*QualifierTranslator.hBaseQualifier(tempQualifiers[1], metaDataType);*/

            String value = cacheReader.read(metaDataKeySchema.dictEntryValue(cdaVersion, Long.toString(dictId), actualData));

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            // date value looks dirty sometimes
            if (metaDataType.startsWith("D")) {
                actualData = actualData.replace(".0", "");
            }

            /*return new String[]{QualifierTranslator.hBaseQualifier(metaData, metaDataType), actualData};*/
            return new String[]{metaData, actualData};
        }
    }
}
