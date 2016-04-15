package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:28
 */
public class XProfileIndicesRepoImpl implements XProfileIndicesRepoCustom {
    @Resource
    SolrTemplate solrTemplate;

    @Override
    public Page<ProfileIndices> search(Query query) {
        Page<ProfileIndices> result = solrTemplate.queryForPage(query, ProfileIndices.class);

        return result;
    }
}
