package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:29
 */
public interface XProfileIndicesRepo extends SolrCrudRepository<ProfileIndices, String>, XProfileIndicesSearch {
    List<ProfileIndices> findByDemographicIdAndEventDateBetween(String demographicId, Date since, Date to);
}
