package com.yihu.ehr.profile.persist.repo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.ProfileGenerator;
import com.yihu.ehr.profile.util.ProfileUtil;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Iterator;

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
        // 先存档案
        TableBundle profileBundle = new TableBundle();
        profileBundle.addValues(profile.getId(), ProfileFamily.Basic, ProfileUtil.getBasicFamilyCellMap(profile));
        hbaseDao.saveOrUpdate(ProfileUtil.Table, profileBundle);

        for (StdDataSet dataSet : profile.getDataSets()) {
            TableBundle dataSetBundle = new TableBundle();
            for (String rowkey : dataSet.getRecordKeys()) {
                dataSetBundle.addValues(
                        rowkey,
                        DataSetFamily.Basic,
                        DataSetUtil.getBasicFamilyQualifier(profile.getId(), dataSet));
                dataSetBundle.addValues(
                        rowkey,
                        DataSetFamily.MetaData,
                        DataSetUtil.getMetaDataFamilyQualifier(rowkey, dataSet));

                if (dataSet instanceof LinkDataSet) {
                    dataSetBundle.addValues(
                            rowkey,
                            DataSetFamily.Extension,
                            DataSetUtil.getExtensionFamilyQualifier((LinkDataSet) dataSet));
                }
            }

            hbaseDao.saveOrUpdate(dataSet.getCode(), dataSetBundle);
        }
    }

    /**
     * 获取档案
     *
     * @param profileId
     * @return
     */
    public Pair<StdProfile, String> findOne(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        TableBundle profileTableBundle = new TableBundle();
        profileTableBundle.addRows(profileId);

        Object[] results = hbaseDao.get(ProfileUtil.Table, profileTableBundle);
        if (results == null) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil(results[0]);

        String cardId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CardId, null);
        String orgCode = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.OrgCode, null);
        String patientId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.PatientId, null);
        String eventNo = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventNo, null);
        String eventDate = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventDate, null);
        String eventType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventType, null);
        String profileType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.ProfileType, ProfileType.Standard.toString());
        String demographicId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DemographicId, null);
        String createDate = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CreateDate, null);
        String cdaVersion = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CdaVersion, null);
        String dataSets = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DataSets, null);

        ProfileType pType = ProfileType.create(profileType);
        EventType eType = StringUtils.isEmpty(eventType) ? null : EventType.create(eventType);

        StdProfile profile = ProfileGenerator.generate(pType);
        profile.setId(profileId);
        profile.setCardId(cardId);
        profile.setOrgCode(orgCode);
        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setEventDate(DateTimeUtils.utcDateTimeParse(eventDate));
        profile.setEventType(eType);
        profile.setProfileType(pType);
        profile.setDemographicId(demographicId);
        profile.setCreateDate(DateTimeUtils.utcDateTimeParse(createDate));
        profile.setCdaVersion(cdaVersion);

        return new ImmutablePair<>(profile, dataSets);
    }

    public void delete(String profileId) throws IOException {
        TableBundle profileTableBundle = new TableBundle();
        profileTableBundle.addRows(profileId);

        Object[] results = hbaseDao.get(ProfileUtil.Table, profileTableBundle);
        if (results == null) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil(results[0]);
        String dataSets = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DataSets, null);

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
