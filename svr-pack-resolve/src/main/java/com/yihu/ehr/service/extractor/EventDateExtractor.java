package com.yihu.ehr.service.extractor;

import com.yihu.ehr.service.ProfileDataSet;
import com.yihu.ehr.util.DateFormatter;

import java.text.ParseException;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
public class EventDateExtractor extends KeyDataExtractor {
    // 摘要数据集
    private static final String SummaryDataSet = null;

    private static final String InnerCodeEventDate = "HDSD00_01_457_D;HDSD00_01_185_D";

    @Override
    public Object extract(ProfileDataSet dataSet, Filter filter) throws ParseException {
        if (filter == Filter.EventDate && SummaryDataSet.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                Map<String, String> record = dataSet.getRecord(key);
                for (String recordKey : record.keySet()) {
                    if (InnerCodeEventDate.contains(recordKey)) {
                        String value = record.get(recordKey);
                        if (value != null) {
                            return DateFormatter.simpleDateTimeParse(value);
                        }
                    }
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
