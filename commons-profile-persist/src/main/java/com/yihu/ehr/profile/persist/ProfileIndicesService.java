package com.yihu.ehr.profile.persist;

import com.yihu.ehr.profile.persist.repo.ProfileIndicesRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 档案索引服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Service
public class ProfileIndicesService {
    @Autowired
    ProfileIndicesRepository indicesRepo;

    private final static String ProfileCore = "HealthArchives";

    /**
     * 按人口学索引搜索。
     *
     * @param demographicId
     * @param since
     * @param to
     * @param pageable
     * @return
     */
    public Page<ProfileIndices> findByDemographic(String demographicId, Date since, Date to, Pageable pageable) {
        return indicesRepo.findByDemographicIdAndEventDate(demographicId, since, to, pageable);
    }

    /**
     * 按患者的机构索引搜索，如：机构代码，患者ID，事件号等
     *
     * @param orgCode
     * @param patientId
     * @param eventNo
     * @param since
     * @param to
     * @return
     */
    public Page<ProfileIndices> findByOrganizationIndices(String orgCode,
                                                          String patientId,
                                                          String eventNo,
                                                          Date since,
                                                          Date to,
                                                          Pageable pageable) {
        Criteria criteria = null;

        // 机构代码优先缩小搜索范围
        if (StringUtils.isNotEmpty(orgCode)) {
            criteria = new Criteria("rowkey").contains(orgCode);
        }

        // 患者ID与事件号可直接定位档案范围
        if (StringUtils.isNotEmpty(patientId) && StringUtils.isNotEmpty(eventNo)) {
            if (null != criteria) criteria = criteria.connect();

            criteria = criteria.and(new Criteria("rowkey").contains(patientId, eventNo));
            return indicesRepo.query(ProfileCore, criteria, ProfileIndices.class);
        }

        return null;
    }

    /**
     * 按人口学索引搜索。
     *
     * @param orgCode
     * @param name
     * @param telephone
     * @param gender
     * @param birthday
     * @return
     */
    public Page<ProfileIndices> findByDemographic(String demographicId,
                                                  String orgCode,
                                                  String name,
                                                  String telephone,
                                                  String gender,
                                                  Date birthday,
                                                  Date since,
                                                  Date to,
                                                  Pageable pageable) {
        Criteria criteria = null;

        // 机构代码优先缩小搜索范围
        if (StringUtils.isNotEmpty(orgCode)) {
            criteria = new Criteria("rowkey").contains(orgCode);
        }

        if (null != criteria) criteria = criteria.connect();

        // 身份证可直接定位档案范围
        if (StringUtils.isNotEmpty(demographicId)) {
            criteria = new Criteria(Demographic.IdCardNo).contains(demographicId);

            return indicesRepo.query(Demographic.DemographicCore, criteria, ProfileIndices.class);
        }

        if (StringUtils.isNotEmpty(name)) {
            criteria = new Criteria(Demographic.LegacyName).contains(name).or(new Criteria(Demographic.Name).contains(name));

            if (StringUtils.isNotEmpty(telephone)) {
                criteria = criteria.connect();
                criteria.and(new Criteria(Demographic.LegacyTelephone).contains(telephone).or(new Criteria(Demographic.Telephone).contains(telephone)));
            } else if (StringUtils.isNotEmpty(gender) && birthday != null) {
                criteria = criteria.connect();
                criteria = criteria.and(new Criteria(Demographic.LegacyGender).contains(gender).or(new Criteria(Demographic.Gender).contains(gender)));

                criteria = criteria.connect();
                criteria = criteria.and(new Criteria(Demographic.Birthday).between(DateUtils.addDays(birthday, -3), DateUtils.addDays(birthday, 3)));
            }
        }

        if (criteria == null) return null;

        Page<Demographic> demographics = indicesRepo.query(Demographic.DemographicCore, criteria, Demographic.class);
        if (demographics.getContent().size() == 0) return null;

        String rowkeys[] = new String[demographics.getContent().size()];
        for (int i = 0; i < rowkeys.length; ++i) {
            rowkeys[i] = demographics.getContent().get(i).getProfileId();
        }

        criteria = new Criteria("rowkey").contains(rowkeys);
        return indicesRepo.query(ProfileCore, criteria, ProfileIndices.class);
    }


}
