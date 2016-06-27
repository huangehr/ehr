package com.yihu.ehr.profile.legacy.persist.repo;

import com.yihu.ehr.profile.legacy.persist.ProfileIndices;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.stereotype.Service;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:29
 */
@Service
public class ProfileIndicesRepository extends SimpleSolrRepository<ProfileIndices, String> {
    @Autowired
    SolrTemplate solrTemplate;


    public <T> Page<T> query(String core, Criteria criteria, Class<T> beanCls) {
        CloudSolrClient cloudSolrClient = (CloudSolrClient) solrTemplate.getSolrClient();
        cloudSolrClient.setDefaultCollection(core);
        return solrTemplate.queryForPage(new SimpleQuery(criteria), beanCls);
    }
}
