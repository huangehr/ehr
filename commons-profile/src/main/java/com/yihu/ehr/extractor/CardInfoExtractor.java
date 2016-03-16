package com.yihu.ehr.extractor;

import com.yihu.ehr.profile.ProfileDataSet;

import java.text.ParseException;
import java.util.Map;
import java.util.Properties;

/**
 * 卡号提取。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
public class CardInfoExtractor extends KeyDataExtractor {
    private static final String CardInfoContainedDataSet = null;
    private static final String InnerCodeCardType = "";
    private static final String InnerCodeCardNo = "";
    private static final String DictEntryCodeCardInfo = "";

    /**
     * 获取此数据集中的卡信息.
     *
     * @return
     */
    @Override
    public Object extract(ProfileDataSet dataSet, Filter filter) throws ParseException {
        if (filter == Filter.CardInfo && CardInfoContainedDataSet.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                Map<String, String> record = dataSet.getRecord(key);
                String value = record.get(InnerCodeCardType);
                if (value != null) {
                    if (DictEntryCodeCardInfo.contains(value)) {
                        Properties properties = new Properties();
                        properties.setProperty("CardNo", record.get(InnerCodeCardNo));
                        properties.setProperty("CardType", record.get(InnerCodeCardType));

                        return properties;
                    }
                }
            }
        }

        if (nextExtractor != null) {
            return nextExtractor.extract(dataSet, filter);
        } else {
            return null;
        }
    }
}
