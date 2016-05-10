package com.yihu.ehr.profile.util;

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
        map.put(ProfileFamily.BasicColumns.CardId.toString(), profile.getCardId());
        map.put(ProfileFamily.BasicColumns.OrgCode.toString(), profile.getOrgCode());
        map.put(ProfileFamily.BasicColumns.PatientId.toString(), profile.getPatientId());
        map.put(ProfileFamily.BasicColumns.EventNo.toString(), profile.getEventNo());
        map.put(ProfileFamily.BasicColumns.EventDate.toString(), DateTimeUtils.utcDateTimeFormat(profile.getEventDate()));
        map.put(ProfileFamily.BasicColumns.EventType.toString(), Integer.toString(profile.getEventType().ordinal()));
        map.put(ProfileFamily.BasicColumns.ProfileType.toString(), Integer.toString(profile.getProfileType().ordinal()));
        map.put(ProfileFamily.BasicColumns.DemographicId.toString(), profile.getDemographicId() == null ? "" : profile.getDemographicId());
        map.put(ProfileFamily.BasicColumns.CreateDate.toString(), DateTimeUtils.utcDateTimeFormat(profile.getCreateDate()));
        map.put(ProfileFamily.BasicColumns.DataSets.toString(), profile.getDataSetIndices());
        map.put(ProfileFamily.BasicColumns.CdaVersion.toString(), profile.getCdaVersion());

        return map;
    }
}
