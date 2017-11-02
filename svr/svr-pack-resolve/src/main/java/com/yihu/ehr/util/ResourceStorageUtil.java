package com.yihu.ehr.util;

import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.family.SubResourceFamily;
import com.yihu.ehr.service.resource.stage2.MasterRecord;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.resource.stage2.SubRecord;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 18:11
 */
public class ResourceStorageUtil {

    /**
     * 取得主表的单元格记录。
     *
     * @param resourceBucket
     * @return
     */
    public static Map<String, String> getMasterResCells(String family, ResourceBucket resourceBucket) {
        Map<String, String> map = new HashMap<>();
        if (family.equals(MasterResourceFamily.Basic)){
            map.put(MasterResourceFamily.BasicColumns.CardId, resourceBucket.getCardId());
            map.put(MasterResourceFamily.BasicColumns.CardType, resourceBucket.getCardType());
            map.put(MasterResourceFamily.BasicColumns.OrgCode, resourceBucket.getOrgCode());
            map.put(MasterResourceFamily.BasicColumns.OrgName, resourceBucket.getOrgName());
            map.put(MasterResourceFamily.BasicColumns.OrgArea, resourceBucket.getOrgArea());
            map.put(MasterResourceFamily.BasicColumns.PatientId, resourceBucket.getPatientId());
            map.put(MasterResourceFamily.BasicColumns.PatientName, resourceBucket.getPatientName());
            map.put(MasterResourceFamily.BasicColumns.Diagnosis, resourceBucket.getDiagnosis());
            map.put(MasterResourceFamily.BasicColumns.HealthProblem, resourceBucket.getHealthProblem());
            map.put(MasterResourceFamily.BasicColumns.EventNo, resourceBucket.getEventNo());
            map.put(MasterResourceFamily.BasicColumns.EventDate, DateTimeUtil.utcDateTimeFormat(resourceBucket.getEventDate()));
            map.put(MasterResourceFamily.BasicColumns.EventType, StringUtils.isEmpty(resourceBucket.getEventType())?"":Integer.toString(resourceBucket.getEventType().ordinal()));
            map.put(MasterResourceFamily.BasicColumns.ProfileType, Integer.toString(resourceBucket.getProfileType().ordinal()));
            String demographicId  = resourceBucket.getDemographicId();
            if(StringUtils.isEmpty(demographicId) || demographicId.equals("null")) {
                demographicId = "";
            }
            map.put(MasterResourceFamily.BasicColumns.DemographicId, demographicId);
            map.put(MasterResourceFamily.BasicColumns.ClientId, resourceBucket.getClientId());
            map.put(MasterResourceFamily.BasicColumns.CreateDate, DateTimeUtil.utcDateTimeFormat(new Date()));
            map.put(MasterResourceFamily.BasicColumns.CdaVersion, resourceBucket.getCdaVersion());
        } else if (family.equals(MasterResourceFamily.Data)){
            MasterRecord masterRecord = resourceBucket.getMasterRecord();
            map = masterRecord.getDataGroup();
        }
        return map;
    }

    public static Map<String, String> getSubResCells(String family, SubRecord subRecord){
        Map<String, String> map = new HashMap<String, String>();
        if (family.equals(SubResourceFamily.Basic)){
            map.put(SubResourceFamily.BasicColumns.ProfileId, subRecord.getProfileId());
        } else if (family.equals(SubResourceFamily.Data)){
            map = subRecord.getDataGroup();
        }
        return map;
    }
}
