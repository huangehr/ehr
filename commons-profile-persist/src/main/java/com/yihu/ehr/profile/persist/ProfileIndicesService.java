package com.yihu.ehr.profile.persist;

import com.yihu.ehr.profile.persist.repo.XProfileIndicesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    XProfileIndicesRepo profileIndicesRepo;

    public List<ProfileIndices> findByDemographicIdAndEventDateBetween(String demographicId, Date since, Date to) {
        return profileIndicesRepo.findByDemographicIdAndEventDateBetween(demographicId, since, to);
    }

    public Page<ProfileIndices> search(String queryString) {
        Criteria criteria = buildCriteria(queryString);

        return profileIndicesRepo.search(new SimpleQuery(criteria));
    }

    private Criteria buildCriteria(String query) {
        Criteria criteria = new Criteria();

        String conditions[] = query.split(";");
        for (String condition : conditions){
            if (condition.contains("demographicId")){
                criteria = criteria.or("demographic_id").contains("412726195111306268");
            }

            if (condition.contains("eventDate")){
                criteria = criteria.or("event_date").between("2015-01-01", "2017-01-01");
            }
        }
        return null;
    }
}
