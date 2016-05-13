package com.yihu.ehr.profile.controller.profile.converter;

import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.model.profile.MOriginFile;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.OriginFile;
import com.yihu.ehr.profile.core.CdaDocument;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.service.Template;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tomcat.jni.File;
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
    String fastDFSPublicServer;

    protected void convertDocuments(StdProfile profile, MProfile mProfile, boolean containDataSet) {
        if (profile instanceof FileProfile) {
            FileProfile fileProfile = (FileProfile) profile;
            for (String cdaDocumentId : fileProfile.getDocuments().keySet()) {
                CdaDocument cdaDocument = fileProfile.getDocuments().get(cdaDocumentId);
                MProfileDocument document = new MProfileDocument();

                document.setId(cdaDocument.getId());
                document.setName("");
                document.setTemplateId(0);

                for (OriginFile originFile : cdaDocument.getOriginFiles()) {
                    MOriginFile mOriginFile = new MOriginFile();
                    mOriginFile.setMime(originFile.getMime());
                    mOriginFile.setOriginUrl(originFile.getOriginUrl());
                    mOriginFile.setExpireDate(originFile.getExpireDate());

                    Map<String, String> files = originFile.getFileUrls();
                    for (String name : files.keySet()) {
                        String path = files.get(name);
                        mOriginFile.getFiles().put(name, path);
                    }

                    document.getInstances().add(mOriginFile);
                }

                mProfile.getDocuments().add(document);
            }
        }

        // TODO 若原档案包中包含有数据集，那此处需要将数据集解析到CDA文档中
    }

    public MProfileDocument convertDocument(StdProfile profile, String cdaDocumentId, boolean containDataSet) {
        FileProfile fileProfile = (FileProfile) profile;

        MProfileDocument profileDocument = new MProfileDocument();
        profileDocument.setId(cdaDocumentId);
        profileDocument.setName("");
        profileDocument.setTemplateId(0);

        for (String rowkey : fileProfile.getDocuments().keySet()){
            CdaDocument cdaDocument = fileProfile.getDocuments().get(rowkey);
            if (!cdaDocument.getId().equals(cdaDocumentId)) continue;

            for (OriginFile originFile : cdaDocument.getOriginFiles()){
                MOriginFile mOriginFile = new MOriginFile();
                mOriginFile.setMime(originFile.getMime());
                mOriginFile.setOriginUrl(originFile.getOriginUrl());
                mOriginFile.setExpireDate(originFile.getExpireDate());

                for (String fileName : originFile.getFileUrls().keySet()){
                    mOriginFile.getFiles().put(fileName, fastDFSPublicServer + "/" + originFile.getFileUrls().get(fileName));
                }

                profileDocument.getInstances().add(mOriginFile);
            }

            break;
        }

        return profileDocument;
    }
}
