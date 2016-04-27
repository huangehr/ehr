package com.yihu.ehr.profile.core.commons;

import com.yihu.ehr.profile.core.StructedProfile;
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

    public static Map<String, String> getBasicFamilyCellMap(StructedProfile profile) {
        Map<String, String> map = new HashMap<>();
        map.put(ProfileFamily.BasicQualifier.CardId.toString(), profile.getCardId());
        map.put(ProfileFamily.BasicQualifier.OrgCode.toString(), profile.getOrgCode());
        map.put(ProfileFamily.BasicQualifier.PatientId.toString(), profile.getPatientId());
        map.put(ProfileFamily.BasicQualifier.EventNo.toString(), profile.getEventNo());
        map.put(ProfileFamily.BasicQualifier.EventDate.toString(), DateTimeUtils.utcDateTimeFormat(profile.getEventDate()));
        map.put(ProfileFamily.BasicQualifier.EventType.toString(), profile.getEventType().toString());
        map.put(ProfileFamily.BasicQualifier.ProfileType.toString(), profile.getProfileType().toString());
        map.put(ProfileFamily.BasicQualifier.Summary.toString(), profile.getSummary());
        map.put(ProfileFamily.BasicQualifier.DemographicId.toString(), profile.getDemographicId() == null ? "" : profile.getDemographicId());
        map.put(ProfileFamily.BasicQualifier.CreateDate.toString(), DateTimeUtils.utcDateTimeFormat(profile.getCreateDate()));
        map.put(ProfileFamily.BasicQualifier.DataSets.toString(), profile.getDataSetIndices());
        map.put(ProfileFamily.BasicQualifier.CdaVersion.toString(), profile.getCdaVersion());

        return map;
    }
}
