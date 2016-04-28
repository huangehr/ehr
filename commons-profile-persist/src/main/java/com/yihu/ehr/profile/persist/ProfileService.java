package com.yihu.ehr.profile.persist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.profile.ProfileType;
import com.yihu.ehr.profile.core.profile.StandardProfile;
import com.yihu.ehr.profile.core.profile.StdDataSet;
import com.yihu.ehr.profile.core.util.DataSetUtil;
import com.yihu.ehr.profile.persist.repo.DataSetRepository;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

/**
 * 档案服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Service
public class ProfileService {
    @Autowired
    ProfileRepository profileRepo;

    @Autowired
    DataSetRepository dataSetRepo;

    @Autowired
    ObjectMapper objectMapper;

    public void saveProfile(StandardProfile profile) throws IOException {
        profileRepo.save(profile);
    }

    /**
     * 获取一个档案。
     *
     * @param profileId
     * @param loadStdDataSet
     * @param loadOriginDataSet
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public StandardProfile getProfile(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        Pair<StandardProfile, String> result = profileRepo.findOne(profileId, loadStdDataSet, loadOriginDataSet);
        StandardProfile profile = result.getLeft();
        profile.determineEventType();

        String cdaVersion = profile.getCdaVersion();
        ProfileType pType = profile.getProfileType();

        // 加载数据集列表
        JsonNode root = objectMapper.readTree(result.getRight());
        Iterator<String> iterator = root.fieldNames();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            String[] rowKeys = root.get(dataSetCode).asText().split(",");

            if (loadStdDataSet || loadOriginDataSet) {
                if (loadStdDataSet) {
                    if (!dataSetCode.contains(DataSetUtil.OriginDataSetFlag)) {
                        Pair<String, StdDataSet> pair = dataSetRepo.findOne(cdaVersion, dataSetCode, pType, rowKeys);
                        profile.insertDataSet(pair.getLeft(), pair.getRight());
                    }
                }
                if (loadOriginDataSet) {
                    if (dataSetCode.contains(DataSetUtil.OriginDataSetFlag)) {
                        Pair<String, StdDataSet> pair = dataSetRepo.findOne(cdaVersion, dataSetCode, pType, rowKeys);
                        profile.insertDataSet(pair.getLeft(), pair.getRight());
                    }
                }
            } else {
                Pair<String, StdDataSet> pair = dataSetRepo.findIndices(cdaVersion, dataSetCode, rowKeys);
                profile.insertDataSet(pair.getLeft(), pair.getRight());
            }
        }


        return profile;
    }
}
