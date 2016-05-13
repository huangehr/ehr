package com.yihu.ehr.profile.persist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.persist.repo.FileRepository;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.persist.repo.DataSetRepository;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

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
    FileRepository fileRepo;

    @Autowired
    ObjectMapper objectMapper;

    public void saveProfile(StdProfile profile) throws IOException {
        // 档案主表
        profileRepo.save(profile);

        // 数据元
        dataSetRepo.save(profile);

        // 存储文件记录
        fileRepo.save(profile);
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
    public StdProfile getProfile(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws Exception {
        // 读取档案主体
        ProfileRepository.ProfileTuple tuple = profileRepo.findOne(profileId);
        StdProfile profile = tuple.getProfile();
        String cdaVersion = profile.getCdaVersion();
        ProfileType pType = profile.getProfileType();

        // 读取数据集
        JsonNode root = objectMapper.readTree(tuple.getDataSetIndices());
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

        // 读取非结构化档案文件列表
        if (profile.getProfileType() == ProfileType.File){
            String[] fileRowKeys = tuple.getFileIndices().split(";");
            ((FileProfile)profile).setDocuments(fileRepo.findAll(fileRowKeys));
        }

        profile.determineEventType();
        if(StringUtils.isEmpty(profile.getClientId())) profile.setClientId("kHAbVppx44");

        return profile;
    }

    public void deleteProfile(String profileId) throws IOException {
        profileRepo.delete(profileId);
    }
}
