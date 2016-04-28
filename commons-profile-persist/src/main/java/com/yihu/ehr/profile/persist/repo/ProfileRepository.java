package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.core.profile.*;
import com.yihu.ehr.data.hbase.CellBundle;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.profile.core.util.*;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

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

    public void save(StandardProfile profile) throws IOException {
        // 先存档案
        CellBundle profileBundle = new CellBundle();
        profileBundle.addValues(profile.getId(), ProfileFamily.Basic, ProfileUtil.getBasicFamilyCellMap(profile));
        hbaseDao.saveOrUpdate(ProfileUtil.Table, profileBundle);

        for (StdDataSet dataSet : profile.getDataSets()) {
            CellBundle dataSetBundle = new CellBundle();
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
    public Pair<StandardProfile, String> findOne(String profileId, boolean loadStdDataSet, boolean loadOriginDataSet) throws IOException, ParseException {
        CellBundle profileCellBundle = new CellBundle();
        profileCellBundle.addRow(profileId);

        Object[] results = hbaseDao.get(ProfileUtil.Table, profileCellBundle);
        if (results.length != 1) throw new RuntimeException("Profile not found.");

        ResultUtil record = new ResultUtil(results[0]);

        String cardId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CardId);
        String orgCode = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.OrgCode);
        String patientId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.PatientId);
        String eventNo = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventNo);
        String eventDate = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventDate);
        String eventType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.EventType);
        String profileType = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.ProfileType);
        String demographicId = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DemographicId);
        String createDate = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CreateDate);
        String cdaVersion = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.CdaVersion);
        String dataSets = record.getCellValue(ProfileFamily.Basic, ProfileFamily.BasicQualifier.DataSets);

        profileType = StringUtils.isEmpty(profileType) ? Integer.toString(ProfileType.Standard.getType()) : profileType;

        StandardProfile profile = ProfileGenerator.generate(ProfileType.valueOf(profileType));
        profile.setId(profileId);
        profile.setCardId(cardId);
        profile.setOrgCode(orgCode);
        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setEventDate(DateTimeUtils.utcDateTimeParse(eventDate));
        profile.setEventType(EventType.valueOf(eventType));
        profile.setProfileType(ProfileType.valueOf(profileType));
        profile.setDemographicId(demographicId);
        profile.setCreateDate(DateTimeUtils.utcDateTimeParse(createDate));
        profile.setCdaVersion(cdaVersion);

        return new ImmutablePair<>(profile, dataSets);
    }
}
