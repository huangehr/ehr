package com.yihu.ehr.profile.persist;

import com.yihu.ehr.profile.persist.repo.XProfileIndicesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.Query;
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

    public List<ProfileIndices> findByDemographicIdAndEventDateBetween(String demographicId, Date since, Date to){
        return profileIndicesRepo.findByDemographicIdAndEventDateBetween(demographicId, since, to);
    }

    public List<ProfileIndices> search(String queryString){
        Query query = buildQuery(queryString);

        return profileIndicesRepo.search(query);
    }

    private Query buildQuery(String queryString){
        return null;
    }
}
