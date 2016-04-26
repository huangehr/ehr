package com.yihu.ehr.extractor;

import com.yihu.ehr.profile.core.StdDataSet;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * 卡号提取。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
@Component
public class CardInfoExtractor extends KeyDataExtractor {
    private String CardInfoContainedDataSet;

    private String InnerCodeCardType = "";

    private String InnerCodeCardNo = "";

    private String DictEntryCodeCardInfo = "";

    /**
     * 获取此数据集中的卡信息.
     *
     * @return
     */
    @Override
    public Object extract(StdDataSet dataSet, Filter filter) throws ParseException {
        return null;

        /*if (filter == Filter.CardInfo && CardInfoContainedDataSet.contains(dataSet.getCode())) {
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
        }*/
    }
}
