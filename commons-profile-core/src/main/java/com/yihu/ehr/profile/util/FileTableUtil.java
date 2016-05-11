package com.yihu.ehr.profile.util;

import com.yihu.ehr.profile.core.FileFamily;
import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.RawDocument;
import com.yihu.ehr.util.DateTimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.04.15
 */
public class FileTableUtil {
    public static final String Table = "RawFiles";

    public static Map<String, String> getBasicFamilyCellMap(FileProfile profile) {
        Map<String, String> map = new HashMap<>();
        map.put(FileFamily.BasicColumns.ProfileId, profile.getId());
        map.put(FileFamily.BasicColumns.PatientId, profile.getPatientId());
        map.put(FileFamily.BasicColumns.EventNo, profile.getEventNo());
        map.put(FileFamily.BasicColumns.CdaVersion, profile.getCdaVersion());
        map.put(FileFamily.BasicColumns.OrgCode, profile.getOrgCode());

        return map;
    }

    public static Map<String, String> getFileFamilyCellMap(RawDocument document){
        Map<String, String> map = new HashMap<>();
        map.put(FileFamily.FileColumns.CdaDocumentId, document.getCdaDocumentId());
        map.put(FileFamily.FileColumns.OriginUrl, document.getOriginUrl());
        map.put(FileFamily.FileColumns.ExpireDate, DateTimeUtils.utcDateTimeFormat(document.getExpireDate()));
        map.put(FileFamily.FileColumns.Files, document.formatStorageUrls());

        return map;
    }
}
