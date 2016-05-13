package com.yihu.ehr.profile.controller.profile.converter;

import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.StdProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sand
 * @created 2016.05.09 17:13
 */
@Service
public class ProfileUtil {
    @Autowired
    private StdProfileConverter stdProfileConverter;

    @Autowired
    private FileProfileConverter fileProfileConverter;

    public MProfile convertProfile(StdProfile profile, boolean containDataSet) {
        if (profile instanceof FileProfile){
            return fileProfileConverter.convertProfile(profile, containDataSet);
        } else if (profile instanceof StdProfile){
            return stdProfileConverter.convertProfile(profile, containDataSet);
        }

        return null;
    }

    public MProfileDocument convertDocument(StdProfile profile, String documentId, boolean containDataSet){
        if (profile instanceof FileProfile){
            return fileProfileConverter.convertDocument(profile, documentId, containDataSet);
        } else if (profile instanceof StdProfile){
            return stdProfileConverter.convertDocument(profile, documentId, containDataSet);
        }

        return null;
    }
}
