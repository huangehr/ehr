package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:29
 */
@Service
public class XProfileIndicesRepo extends SimpleSolrRepository<ProfileIndices, String> {
    @Autowired
    SolrTemplate solrTemplate;

    public Page<ProfileIndices> findByDemographicIdAndEventDate(String demographicId, Date since, Date to, Pageable pageable){
        return null;
    }

    public <T> Page<T> query(String core, Criteria criteria, Class<T> beanCls) {
        return solrTemplate.queryForPage(new SimpleQuery(criteria), beanCls);
    }
}
