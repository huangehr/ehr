package com.yihu.ehr.profile.legacy.sanofi.persist;

import com.yihu.ehr.profile.legacy.sanofi.persist.repo.ProfileIndicesRepository;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
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

//    private final static String ProfileCore = "HealthArchives";


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
            //开始时间和结束时间缩小范围
//            if (since!=null & to!=null) {
//                String rowKeys[] = eventDateFilter(since,to);
//                if(rowKeys.length>0){
//                    criteria.contains(rowKeys);
//                }
//            }
            if (StringUtils.isNotEmpty(telephone)) {
                criteria = criteria.connect();
                criteria.and(new Criteria(Demographic.LegacyTelephone).contains(telephone).or(new Criteria(Demographic.Telephone).contains(telephone)));
                return indicesRepo.query(Demographic.DemographicCore, criteria, ProfileIndices.class);
            } else if (StringUtils.isNotEmpty(gender) && birthday != null) {
                criteria = criteria.connect();
                criteria = criteria.and(new Criteria(Demographic.LegacyGender).contains(gender).or(new Criteria(Demographic.Gender).contains(gender)));

                criteria = criteria.connect();
//                criteria = criteria.and(new Criteria(Demographic.Birthday).between(DateTimeUtil.utcDateTimeFormat(DateUtils.addDays(birthday, -3)), DateTimeUtil.utcDateTimeFormat(DateUtils.addDays(birthday, 3))));
                criteria = criteria.and(new Criteria(Demographic.Birthday).between(DateTimeUtil.utcDateTimeFormat(birthday), DateTimeUtil.utcDateTimeFormat(birthday)));
                return indicesRepo.query(Demographic.DemographicCore, criteria, ProfileIndices.class);
            }
        }

        return indicesRepo.query(Demographic.DemographicCore, criteria, ProfileIndices.class);


//        if (criteria == null) return null;
//
//        Page<Demographic> demographics = indicesRepo.query(Demographic.DemographicCore, criteria, Demographic.class);
//        if (demographics.getContent().size() == 0) return null;
//
//        String rowkeys[] = new String[demographics.getContent().size()];
//        for (int i = 0; i < rowkeys.length; ++i) {
//            rowkeys[i] = demographics.getContent().get(i).getProfileId();
//        }
//
//        criteria = new Criteria("rowkey").contains(rowkeys);

//        return indicesRepo.query(ProfileCore, criteria, ProfileIndices.class);

    }


//    private String[] eventDateFilter(Date since,Date to){
//        Criteria eventDateCriteria = new Criteria("event_date").between(DateTimeUtil.utcDateTimeFormat(since),DateTimeUtil.utcDateTimeFormat(to));
//        Page<Demographic> demographics = indicesRepo.query(ProfileCore, eventDateCriteria, Demographic.class);
//        String rowKeys[] = new String[demographics.getContent().size()];
//        for (int i = 0; i < rowKeys.length; ++i) {
//            rowKeys[i] = demographics.getContent().get(i).rowkey;
//        }
//        return rowKeys;
//    }



}
