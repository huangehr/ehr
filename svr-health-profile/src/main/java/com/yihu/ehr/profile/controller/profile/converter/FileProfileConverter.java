package com.yihu.ehr.profile.controller.profile.converter;

import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.model.profile.MRawDocument;
import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.RawDocument;
import com.yihu.ehr.profile.core.RawDocumentList;
import com.yihu.ehr.profile.core.StdProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.09 15:54
 */
@Service
public class FileProfileConverter extends StdProfileConverter {
    @Value("${fast-dfs.public-server}")
    String fastDFSUtil;

    protected void convertDocuments(StdProfile profile, MProfile mProfile, boolean containDataSet){
        if (profile instanceof FileProfile){
            FileProfile fileProfile = (FileProfile) profile;
            for (String cdaDocumentId : fileProfile.getDocuments().keySet()){
                MProfileDocument document = new MProfileDocument();
                document.setId(cdaDocumentId);
                document.setName("");
                document.setTemplateId(0);

                mProfile.getDocuments().add(document);

                RawDocumentList rawDocumentList = fileProfile.getDocuments().get(cdaDocumentId);
                for (RawDocument rawDocument : rawDocumentList){
                    MRawDocument mRawDocument = new MRawDocument();
                    mRawDocument.setOriginUrl(rawDocument.getOriginUrl());
                    mRawDocument.setExpireDate(rawDocument.getExpireDate());

                    Map<String, String> files = rawDocument.getStorageUrls();
                    for (String name : files.keySet()){
                        String path = files.get(name);
                        mRawDocument.getFiles().add(name + ":" + path);
                    }

                    document.getRawDocuments().add(mRawDocument);
                }
            }
        }
    }
}
