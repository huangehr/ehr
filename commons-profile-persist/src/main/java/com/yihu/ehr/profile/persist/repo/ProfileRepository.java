package com.yihu.ehr.profile.persist.repo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.FileTableUtil;
import com.yihu.ehr.profile.util.ProfileFactory;
import com.yihu.ehr.profile.util.ProfileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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

    public void save(StdProfile profile) throws IOException {
        TableBundle bundle = new TableBundle();
        bundle.addValues(profile.getId(), ProfileFamily.Basic, ProfileUtil.getBasicFamilyCellMap(profile));
        hbaseDao.saveOrUpdate(ProfileUtil.Table, bundle);
    }

    /**
     * 获取档案.
     *
     * @param profileId
     * @return
     */
    public Pair<StdProfile, String> findOne(String profileId) throws Exception {
        TableBundle profileTableBundle = new TableBundle();
        profileTableBundle.addRows(profileId);

        Object[] results = hbaseDao.get(ProfileUtil.Table, profileTableBundle);
        if (results == null) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil(results[0]);

        String dataSets = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.DataSets, null);
        String eventType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.EventType, null);
        String profileType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.ProfileType, ProfileType.Standard.toString());

        ProfileType pType = ProfileType.create(profileType);
        EventType eType = StringUtils.isEmpty(eventType) ? null : EventType.create(eventType);

        StdProfile profile = ProfileFactory.createProfile(pType);
        HBaseEntityUtil.assembleEntity(results[0], profile);

        profile.setId(profileId);
        profile.setEventType(eType);
        profile.setProfileType(pType);

        return new ImmutablePair<>(profile, dataSets);
    }

    public void delete(String profileId) throws IOException {
        TableBundle profileTableBundle = new TableBundle();
        profileTableBundle.addRows(profileId);

        Object[] results = hbaseDao.get(ProfileUtil.Table, profileTableBundle);
        if (results == null) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil(results[0]);
        String dataSets = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicColumns.DataSets, null);

        // 删除数据集中的数据
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        JsonNode root = objectMapper.readTree(dataSets);

        TableBundle tableBundle = new TableBundle();
        Iterator<String> iterator = root.fieldNames();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            String[] rowKeys = root.get(dataSetCode).asText().split(",");

            tableBundle.clear();
            tableBundle.addRows(rowKeys);

            hbaseDao.delete(dataSetCode, tableBundle);
        }

        // 删除档案中索引
        tableBundle.clear();
        tableBundle.addRows(profileId);

        hbaseDao.delete(ProfileUtil.Table, tableBundle);
    }
}
