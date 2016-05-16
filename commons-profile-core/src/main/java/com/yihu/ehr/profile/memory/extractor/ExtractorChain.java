package com.yihu.ehr.profile.memory.extractor;

import com.yihu.ehr.profile.memory.intermediate.StdDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    @Autowired
    private ApplicationContext context;

    private KeyDataExtractor[] extractors;

    @PostConstruct
    private void initChain(){
        extractors = new KeyDataExtractor[3];
        extractors[0] = context.getBean(IdentityExtractor.class);
        extractors[1] = context.getBean(EventExtractor.class);
        extractors[2] = context.getBean(CardInfoExtractor.class);
    }

    public Object doExtract(StdDataSet dataSet, KeyDataExtractor.Filter filter) throws ParseException {
        for (KeyDataExtractor extractor : extractors){
            Object data = extractor.extract(dataSet, filter);
            if (data != null) return data;
        }

        return null;
    }
}
