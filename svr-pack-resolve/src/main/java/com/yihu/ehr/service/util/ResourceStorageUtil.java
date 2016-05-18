package com.yihu.ehr.service.util;

import com.yihu.ehr.service.resource.*;
import com.yihu.ehr.service.resource.stage2.MasterRecord;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.family.SubResourceFamily;
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
    public static final String MasterTable = "HealthProfile";
    public static final String SubTable = "HealthProfileSub";

    /**
     * 取得主表的单元格记录。
     *
     * @param profile
     * @return
     */
    public static Map<String, String> getMasterResCells(String family, ResourceBucket profile) {
        Map<String, String> map = new HashMap<>();

        if (family.equals(MasterResourceFamily.Basic)){
            map.put(MasterResourceFamily.BasicColumns.CardId, profile.getCardId());
            map.put(MasterResourceFamily.BasicColumns.OrgCode, profile.getOrgCode());
            map.put(MasterResourceFamily.BasicColumns.PatientId, profile.getPatientId());
            map.put(MasterResourceFamily.BasicColumns.EventNo, profile.getEventNo());
            map.put(MasterResourceFamily.BasicColumns.EventDate, DateTimeUtils.utcDateTimeFormat(profile.getEventDate()));
            map.put(MasterResourceFamily.BasicColumns.EventType, Integer.toString(profile.getEventType().ordinal()));
            map.put(MasterResourceFamily.BasicColumns.ProfileType, Integer.toString(profile.getProfileType().ordinal()));
            map.put(MasterResourceFamily.BasicColumns.DemographicId, profile.getDemographicId() == null ? "" : profile.getDemographicId());
            map.put(MasterResourceFamily.BasicColumns.CreateDate, DateTimeUtils.utcDateTimeFormat(new Date()));
            map.put(MasterResourceFamily.BasicColumns.CdaVersion, profile.getCdaVersion());
        } else if (family.equals(MasterResourceFamily.Resource)){
            MasterRecord masterRecord = profile.getMasterRecord();
            map = masterRecord.getDataGroup();
        }

        return map;
    }

    public static Map<String, String> getSubResCellMap(String profileId, StdDataSet dataSet){
        Map<String, String> map = new HashMap<>();
        map.put(SubResourceFamily.BasicColumns.ProfileId.toString(), profileId);
        map.put(SubResourceFamily.BasicColumns.CdaVersion.toString(), dataSet.getCdaVersion());
        map.put(SubResourceFamily.BasicColumns.OrgCode.toString(), dataSet.getOrgCode());
        map.put(SubResourceFamily.BasicColumns.LastUpdateTime.toString(), DateTimeUtils.utcDateTimeFormat(new Date()));

        return map;
    }
}
