package com.yihu.ehr.profile.legacy.sanofi.persist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.legacy.sanofi.memory.model.MemoryProfile;
import com.yihu.ehr.profile.legacy.sanofi.memory.model.StdDataSet;
import com.yihu.ehr.profile.legacy.sanofi.persist.repo.DataSetRepository;
import com.yihu.ehr.profile.legacy.sanofi.persist.repo.ProfileRepository;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 获取一个档案。
     *
     * @param profileId
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public MemoryProfile getProfile(String profileId) throws Exception {
        // 读取档案主体
        ProfileRepository.ProfileTuple tuple = profileRepo.findOne(profileId);
        MemoryProfile profile = tuple.getProfile();
        String cdaVersion = profile.getCdaVersion();
//        ProfileType pType = profile.getProfileType();

        // 读取数据集
        JsonNode root = objectMapper.readTree(tuple.getDataSetIndices());
        Iterator<String> iterator = root.fieldNames();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            String[] rowKeys = root.get(dataSetCode).asText().split(",");

            Pair<String, StdDataSet> pair = dataSetRepo.findIndices(cdaVersion, dataSetCode, rowKeys);
            profile.insertDataSet(pair.getLeft(), pair.getRight());
        }

        profile.determineEventType();
        if(StringUtils.isEmpty(profile.getClientId())) profile.setClientId("kHAbVppx44");

        return profile;
    }

}
