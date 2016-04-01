package com.yihu.ehr.persist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.data.HBaseClient;
import com.yihu.ehr.data.ResultWrapper;
import com.yihu.ehr.data.SolrClient;
import com.yihu.ehr.data.SolrQueryBuilder;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.profile.ProfileTableOptions;
import com.yihu.ehr.util.DateFormatter;
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
    ProfileWriter profileWriter;

    @Autowired
    ProfileLoader profileLoader;

    @Autowired
    private SolrClient solrClient;

    @Autowired
    private HBaseClient hbaseClient;

    public List<Profile> searchProfile(){
        return null;
    }

    public List<Profile> getProfiles(final String demographicId,
                                     final Date from,
                                     final Date to,
                                     boolean loadStdDataSet,
                                     boolean loadOriginDataSet) throws IOException, ParseException {
        String queryString = new SolrQueryBuilder()
                .begin()
                .where(ProfileTableOptions.AcDemographicId).equals(demographicId)
                .and()
                .where(ProfileTableOptions.AcEventDate)
                .between(DateFormatter.utcDateTimeFormat(from), DateFormatter.utcDateTimeFormat(to))
                .end()
                .buildQuery();

        Map<String, String> sorter = new HashMap<>();
        sorter.put(ProfileTableOptions.AcCreateDate, "desc");

        List<String> rowKeys = solrClient.findRowKey(ProfileTableOptions.ProfileTable,
                queryString,
                sorter,
                50,
                1);

        List<Profile> archiveList = new ArrayList<>();
        for(String rowKey : rowKeys){
            Profile profile = profileLoader.load(rowKey, loadStdDataSet, loadOriginDataSet);
            archiveList.add(profile);
        }

        return archiveList;
    }

    public Profile getProfile(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        Profile healthArchive = profileLoader.load(profileId, loadStdDataSet, loadOriginDataSet);

        return healthArchive;
    }

    public ProfileDataSet getDataSet(final String cdaVersion, final String dataSetCode, final String[] rowKeys) throws IOException {
        ProfileDataSet dataSet = profileLoader.loadFullDataSet(cdaVersion, dataSetCode, rowKeys).getRight();

        return dataSet;
    }

    public void saveProfile(Profile profile) throws IOException {
        profileWriter.writeArchive(profile);
    }
    
    public void deleteProfile(String profileId) throws IOException {
        ResultWrapper archive = hbaseClient.getResultAsWrapper(ProfileTableOptions.ProfileTable, profileId);
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
        hbaseClient.deleteRecord(ProfileTableOptions.ProfileTable, profileId);
    }
}
