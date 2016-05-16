package com.yihu.ehr.profile.memory.util;

import com.yihu.ehr.profile.memory.intermediate.*;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 18:11
 */
public class ResourceStorageUtil {
    public static final String Table = "HealthProfiles";

    /**
     * 取得主表的单元格记录。
     *
     * @param profile
     * @return
     */
    public static Map<String, String> getMainRecordBasicFamilyCellMap(MemoryProfile profile) {
        Map<String, String> map = new HashMap<>();
        map.put(ProfileFamily.BasicColumns.CardId, profile.getCardId());
        map.put(ProfileFamily.BasicColumns.OrgCode, profile.getOrgCode());
        map.put(ProfileFamily.BasicColumns.PatientId, profile.getPatientId());
        map.put(ProfileFamily.BasicColumns.EventNo, profile.getEventNo());
        map.put(ProfileFamily.BasicColumns.EventDate, DateTimeUtils.utcDateTimeFormat(profile.getEventDate()));
        map.put(ProfileFamily.BasicColumns.EventType, Integer.toString(profile.getEventType().ordinal()));
        map.put(ProfileFamily.BasicColumns.ProfileType, Integer.toString(profile.getProfileType().ordinal()));
        map.put(ProfileFamily.BasicColumns.DemographicId, profile.getDemographicId() == null ? "" : profile.getDemographicId());
        map.put(ProfileFamily.BasicColumns.CreateDate, DateTimeUtils.utcDateTimeFormat(profile.getCreateDate()));
        map.put(ProfileFamily.BasicColumns.DataSets, profile.getDataSetIndices());
        map.put(ProfileFamily.BasicColumns.CdaVersion, profile.getCdaVersion());
        
        if (profile instanceof MemoryFileProfile){
            map.put(ProfileFamily.BasicColumns.Files, ((MemoryFileProfile)profile).getFileIndices());
        }

        return map;
    }

    public static Map<String, String> getBasicFamilyQualifier(String profileId, StdDataSet dataSet){
        Map<String, String> map = new HashMap<>();
        map.put(DataSetFamily.BasicColumns.ProfileId.toString(), profileId);
        map.put(DataSetFamily.BasicColumns.CdaVersion.toString(), dataSet.getCdaVersion());
        map.put(DataSetFamily.BasicColumns.OrgCode.toString(), dataSet.getOrgCode());
        map.put(DataSetFamily.BasicColumns.LastUpdateTime.toString(), DateTimeUtils.utcDateTimeFormat(new Date()));

        return map;
    }
}
