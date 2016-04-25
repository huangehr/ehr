package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:29
 */
public interface XProfileIndicesRepo extends XProfileIndicesRepoCustom, SolrCrudRepository<ProfileIndices, String> {
    Page<ProfileIndices> findByDemographicIdAndEventDateBetween(String demographicId, Date since, Date to, Pageable pageable);
}
