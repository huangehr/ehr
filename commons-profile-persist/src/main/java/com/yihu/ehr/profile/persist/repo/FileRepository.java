package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.util.FileTableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.09 19:13
 */
@Service
public class FileRepository {
    @Autowired
    HBaseDao hbaseDao;

    public void save(StdProfile profile) throws IOException {
        if (profile.getProfileType() == ProfileType.File){
            TableBundle bundle = new TableBundle();
            if (profile instanceof FileProfile) {
                FileProfile fileProfile = (FileProfile)profile;

                int i = 0;
                Map<String, RawDocumentList> rawDocuments = fileProfile.getDocuments();
                for (String cdaDocumentId : rawDocuments.keySet()){
                    RawDocumentList rawDocumentList = rawDocuments.get(cdaDocumentId);
                    for (RawDocument rawDocument : rawDocumentList){
                        String rowkey = String.format("%s$%s", fileProfile.getId(), i++);

                        bundle.addValues(rowkey, FileFamily.Basic, FileTableUtil.getBasicFamilyCellMap(fileProfile));
                        bundle.addValues(rowkey, FileFamily.Files, FileTableUtil.getFileFamilyCellMap(rawDocument));
                    }
                }

                hbaseDao.saveOrUpdate(FileTableUtil.Table, bundle);
            }
        }
    }

    public Map<String, RawDocumentList> findAll(String[] rowkeys){
        return null;
    }
}
