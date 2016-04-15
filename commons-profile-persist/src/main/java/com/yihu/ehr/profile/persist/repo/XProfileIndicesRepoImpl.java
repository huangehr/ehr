package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:28
 */
@Repository
public class XProfileIndicesRepoImpl implements XProfileIndicesRepoCustom {
    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public List<ProfileIndices> search(Query query) {
        Page result = solrTemplate.queryForPage(query, ProfileIndices.class);

        return result.getContent();
    }
}
