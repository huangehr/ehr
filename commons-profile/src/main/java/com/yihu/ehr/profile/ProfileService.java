package com.yihu.ehr.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.data.HBaseClient;
import com.yihu.ehr.data.ResultWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

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
    private HBaseClient hbaseClient;
    
    public Profile getArchive(String archiveId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        ProfileLoader loader = new ProfileLoader();
        Profile healthArchive = loader.load(archiveId, loadStdDataSet, loadOriginDataSet);

        return healthArchive;
    }

    public ProfileDataSet getDataSet(final String cdaVersion, final String dataSetCode, final String[] rowKeys) throws IOException {
        ProfileLoader profileLoader = new ProfileLoader();
        ProfileDataSet dataSet = profileLoader.loadFullDataSet(cdaVersion, dataSetCode, rowKeys).getRight();

        return dataSet;
    }

    public void saveProfile(Profile profile) throws IOException {
        ProfileWriter writer = new ProfileWriter();
        writer.writeArchive(profile);
    }
    
    public void deleteArchive(String archiveId) throws IOException {
        ResultWrapper archive = hbaseClient.getResultAsWrapper(ProfileTableOptions.ArchiveTable, archiveId);
        if (archive == null) throw new RuntimeException("Profile not found");

        // 先删除数据集
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

        // 删除档案
        hbaseClient.deleteRecord(ProfileTableOptions.ArchiveTable, archiveId);
    }
}
