package com.yihu.ehr.profile.controller.indices;

import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.ProfileIndicesService;
import com.yihu.ehr.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

/**
 * 健康档案工具。
 *
 * @author Sand
 * @created 2016.05.03 14:08
 */
@Service
public class ProfileIndicesUtil {
    @Autowired
    private ProfileIndicesService indicesService;

    public Page<ProfileIndices> searchProfile(MProfileSearch query, Date since, Date to) throws ParseException {
        String demographicId = query.getDemographicId();
        String orgCode = query.getOrganizationCode();
        String patientId = query.getPatientId();
        String eventNo = query.getEventNo();
        String name = query.getName();
        String telephone = query.getTelephone();
        String gender = query.getGender();
        Date birthday = DateTimeUtils.simpleDateParse(query.getBirthday());

        Page<ProfileIndices> profileIndices = indicesService.findByIndices(orgCode, patientId, eventNo, since, to, null);
        if (profileIndices == null || profileIndices.getContent().isEmpty()) {
            profileIndices = indicesService.findByDemographic(demographicId, orgCode, name, telephone, gender, birthday, since, to, null);
        }

        return profileIndices;
    }
}
