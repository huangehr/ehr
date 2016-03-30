package com.yihu.ehr.persist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.data.HBaseClient;
import com.yihu.ehr.data.ResultWrapper;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.profile.ProfileTableOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

/**
 * 健康档案管理器。实现档案、健康事件及数据集的获取接口。也可以搜索某段时间内病人的数据。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 17:56
 */
@Service
public class ProfileService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProfileWriter profileWriter;

    @Autowired
    ProfileLoader profileLoader;

    @Autowired
    private HBaseClient hbaseClient;

    public List<Profile> searchProfile(){
        return null;
    }
    
    public Profile getProfile(String archiveId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        Profile healthArchive = profileLoader.load(archiveId, loadStdDataSet, loadOriginDataSet);

        return healthArchive;
    }

    public ProfileDataSet getDataSet(final String cdaVersion, final String dataSetCode, final String[] rowKeys) throws IOException {
        ProfileDataSet dataSet = profileLoader.loadFullDataSet(cdaVersion, dataSetCode, rowKeys).getRight();

        return dataSet;
    }

    public void saveProfile(Profile profile) throws IOException {
        profileWriter.writeArchive(profile);
    }
    
    public void deleteProfile(String archiveId) throws IOException {
        ResultWrapper archive = hbaseClient.getResultAsWrapper(ProfileTableOptions.ArchiveTable, archiveId);
        if (archive == null) throw new RuntimeException("Profile not found");

        // delete data set first
        String dataSets = archive.getValueAsString(ProfileTableOptions.FamilyBasic, ProfileTableOptions.AcDataSets);
        if (null != dataSets){
            JsonNode root = objectMapper.readTree(dataSets);
            Iterator<String> iterator = root.fieldNames();
            while (iterator.hasNext()) {
                String dataSetCode = iterator.next();
                String[] rowKeys = root.get(dataSetCode).asText().split(",");

                for (String rowKey : rowKeys) {
                    hbaseClient.deleteRecord(dataSetCode, rowKey);
                }
            }
        }

        // delete profile now
        hbaseClient.deleteRecord(ProfileTableOptions.ArchiveTable, archiveId);
    }
}
