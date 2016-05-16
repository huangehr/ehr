package com.yihu.ehr.profile.controller.profile.converter;

import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import com.yihu.ehr.model.profile.MOriginFile;
import com.yihu.ehr.profile.memory.intermediate.MemoryFileProfile;
import com.yihu.ehr.profile.memory.intermediate.MemoryProfile;
import com.yihu.ehr.profile.memory.intermediate.OriginFile;
import com.yihu.ehr.profile.memory.intermediate.CdaDocument;
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

    protected void convertDocuments(MemoryProfile profile, MProfile mProfile, boolean containDataSet) {
        if (profile instanceof MemoryFileProfile) {
            MemoryFileProfile memoryFileProfile = (MemoryFileProfile) profile;
            for (String cdaDocumentId : memoryFileProfile.getDocuments().keySet()) {
                CdaDocument cdaDocument = memoryFileProfile.getDocuments().get(cdaDocumentId);
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

    public MProfileDocument convertDocument(MemoryProfile profile, String cdaDocumentId, boolean containDataSet) {
        MemoryFileProfile memoryFileProfile = (MemoryFileProfile) profile;

        MProfileDocument profileDocument = new MProfileDocument();
        profileDocument.setId(cdaDocumentId);
        profileDocument.setName("");
        profileDocument.setTemplateId(0);

        for (String rowkey : memoryFileProfile.getDocuments().keySet()){
            CdaDocument cdaDocument = memoryFileProfile.getDocuments().get(rowkey);
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
