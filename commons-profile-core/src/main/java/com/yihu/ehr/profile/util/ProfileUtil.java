package com.yihu.ehr.profile.util;

import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.ProfileFamily;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 18:11
 */
public class ProfileUtil {
    public static final String Table = "HealthArchives";

    public static Map<String, String> getBasicFamilyCellMap(StdProfile profile) {
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
        
        if (profile instanceof FileProfile){
            map.put(ProfileFamily.BasicColumns.Files, ((FileProfile)profile).getFileIndices());
        }

        return map;
    }
}
