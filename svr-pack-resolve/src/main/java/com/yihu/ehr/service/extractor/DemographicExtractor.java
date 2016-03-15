package com.yihu.ehr.service.extractor;

import com.yihu.ehr.service.ProfileDataSet;

import java.text.ParseException;
import java.util.Map;

/**
 * 身份证号提取。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
public class DemographicExtractor extends KeyDataExtractor {
    // 可识别身份的数据集
    private static final String DemographicContainedDataSet = null;
    private static final String InnerCodeIdCardType = "HDSA00_01_016_S";
    private static final String InnerCodeIdCardNo = "HDSA00_01_017_S";
    private static final String DictEntryCodeDemographic = "01;02";

    @Override
    public Object extract(ProfileDataSet dataSet, Filter filter) throws ParseException {
        if (filter == Filter.DemographicInfo && DemographicContainedDataSet.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                Map<String, String> record = dataSet.getRecord(key);
                String value = record.get(InnerCodeIdCardType);
                if (value != null) {
                    if (DictEntryCodeDemographic.contains(value)) {
                        return record.get(InnerCodeIdCardNo);
                    }
                } else {
                    return record.get(InnerCodeIdCardNo);
                }
            }
        }

        if (nextExtractor != null) {
            return nextExtractor.extract(dataSet, filter);
        } else {
            return "";
        }
    }
}
