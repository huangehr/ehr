package com.yihu.ehr.extractor;

import com.yihu.ehr.profile.ProfileDataSet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 15:02
 */
@Component
public class ExtractorChain {
    private KeyDataExtractor[] extractors;

    @PostConstruct
    private void initChain(){
        extractors = new KeyDataExtractor[3];
        extractors[0] = new DemographicExtractor();
        extractors[1] = new EventDateExtractor();
        extractors[2] = new CardInfoExtractor();
    }

    public Object doExtract(ProfileDataSet dataSet, KeyDataExtractor.Filter filter) throws ParseException {
        for (KeyDataExtractor extractor : extractors){
            Object data = extractor.extract(dataSet, filter);
            if (data != null) return data;
        }

        return null;
    }
}
