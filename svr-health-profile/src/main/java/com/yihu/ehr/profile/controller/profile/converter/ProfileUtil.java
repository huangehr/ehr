package com.yihu.ehr.profile.controller.profile.converter;

import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sand
 * @created 2016.05.09 17:13
 */
@Service
public class ProfileUtil {
    /*@Autowired
    private StdProfileConverter stdProfileConverter;

    @Autowired
    private FileProfileConverter fileProfileConverter;

    public MProfile convertProfile(MemoryProfile profile, boolean containDataSet) {
        if (profile instanceof MemoryFileProfile){
            return fileProfileConverter.convertProfile(profile, containDataSet);
        } else if (profile instanceof MemoryProfile){
            return stdProfileConverter.convertProfile(profile, containDataSet);
        }

        return null;
    }

    public MProfileDocument convertDocument(MemoryProfile profile, String documentId, boolean containDataSet){
        if (profile instanceof MemoryFileProfile){
            return fileProfileConverter.convertDocument(profile, documentId, containDataSet);
        } else if (profile instanceof MemoryProfile){
            return stdProfileConverter.convertDocument(profile, documentId, containDataSet);
        }

        return null;
    }*/
}
