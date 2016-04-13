package com.yihu.ehr.extractor;

import com.yihu.ehr.profile.core.ProfileDataSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * 身份证号提取。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
@Component
@ConfigurationProperties(prefix="extractor.identity")
public class IdentityExtractor extends KeyDataExtractor {
    private List<String> dataSets = new ArrayList<>();        // 包含身份信息的数据集

    @Value("${extractor.identity.meta-data.id-card-no}")
    private String IdCardNoMetaData;

    @Value("${extractor.identity.meta-data.id-card-type}")
    private String IdCardTypeMetaData;

    private static final String IdCardNoDictEntry = "01;02";    // 身份字典项代码：身份证号与护照

    @Override
    public Object extract(ProfileDataSet profileDataSet, Filter filter) throws ParseException {
        if (filter == Filter.DemographicInfo && dataSets.contains(profileDataSet.getCode())) {
            for (String key : profileDataSet.getRecordKeys()) {
                Map<String, String> record = profileDataSet.getRecord(key);
                String value = record.get(IdCardTypeMetaData);
                if (value != null) {
                    if (IdCardNoDictEntry.contains(value)) {
                        return record.get(IdCardNoMetaData);
                    }
                } else {
                    // default as identity card no
                    return record.get(IdCardNoMetaData);
                }
            }
        }

        return null;
    }

    public List<String> getDataSets() {
        return this.dataSets;
    }
}
