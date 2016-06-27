package com.yihu.ehr.profile.legacy.sanofi.persist.repo;

import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.profile.legacy.sanofi.memory.model.MemoryProfile;
import com.yihu.ehr.profile.legacy.sanofi.memory.model.ProfileFamily;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 健康档案加载器. 可以根据健康档案ID或与之关联的事件ID加载档案.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:20
 */
@Service
public class ProfileRepository {
    @Autowired
    HBaseDao hbaseDao;


    /**
     * 获取档案.
     *
     * @param profileId
     * @return
     */
    public ProfileTuple findOne(String profileId) throws Exception {
        TableBundle profileTableBundle = new TableBundle();
        profileTableBundle.addRows(profileId);

        Object[] results = hbaseDao.get("HealthArchives", profileTableBundle);
        if (results == null) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil(results[0]);

        String dataSetIndices = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.DataSets, null);
        //// TODO: 2016/6/22 文件不考虑
//        String fileIndices = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.Files, null);

        String eventType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.EventType, null);
        String profileType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.ProfileType, ProfileType.Standard.toString());

        ProfileType pType = ProfileType.create(profileType);
        EventType eType = StringUtils.isEmpty(eventType) ? null : EventType.create(eventType);

//        MemoryProfile profile = ProfileFactory.createProfile(pType);
        MemoryProfile profile = new MemoryProfile();
        HBaseEntityUtil.assembleEntity(results[0], profile);

        profile.setId(profileId);
        profile.setEventType(eType);
        profile.setProfileType(pType);

        ProfileTuple tuple = new ProfileTuple();
        tuple.setProfile(profile);
        tuple.setDataSetIndices(dataSetIndices);
//        tuple.setFileIndices(fileIndices);

        return tuple;
    }


    public static class ProfileTuple {
        private MemoryProfile profile;
        private String dataSetIndices;
//        private String fileIndices;

        public MemoryProfile getProfile() {
            return profile;
        }

        public void setProfile(MemoryProfile profile) {
            this.profile = profile;
        }

        public String getDataSetIndices() {
            return dataSetIndices;
        }

        public void setDataSetIndices(String dataSetIndices) {
            this.dataSetIndices = dataSetIndices;
        }

//        public String getFileIndices() {
//            return fileIndices;
//        }
//
//        public void setFileIndices(String fileIndices) {
//            this.fileIndices = fileIndices;
//        }
    }
}
