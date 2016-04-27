package com.yihu.ehr.profile.core.extractor;

import com.yihu.ehr.profile.core.StdDataSet;

import java.text.ParseException;

/**
 * 关键数据提取器。如：身份证号，事件号，卡号等。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:12
 */
public abstract class KeyDataExtractor {

    public enum Filter{
        CardInfo,
        DemographicInfo,
        EventDate,
        EventType
    }

    public abstract Object extract(StdDataSet dataSet, Filter filter) throws ParseException;
}
