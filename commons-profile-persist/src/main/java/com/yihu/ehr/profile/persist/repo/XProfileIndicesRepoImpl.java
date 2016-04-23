package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.core.commons.ProfileId;
import com.yihu.ehr.profile.persist.Demographic;
import com.yihu.ehr.profile.persist.ProfileIndices;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:28
 */
@Repository
public class XProfileIndicesRepoImpl implements XProfileIndicesRepoCustom {
    @Autowired
    private CloudSolrServer cloudSolrServer;

    private Map<String, SolrTemplate> solrTemplateMap = new HashMap<>();

    public XProfileIndicesRepoImpl() {
    }

    private SolrTemplate getSolrTemplate(String core) {
        SolrTemplate solrTemplate = solrTemplateMap.get(core);
        if (null == solrTemplate) {
            solrTemplate = new SolrTemplate(new MulticoreSolrServerFactory(cloudSolrServer));
            solrTemplate.setSolrCore(core);
            solrTemplate.afterPropertiesSet();
            solrTemplateMap.put(core, solrTemplate);
        }

        return solrTemplate;
    }

    public <T> Page<T> query(String core, Criteria criteria, Class<T> beanCls) {
        SolrTemplate solrTemplate = getSolrTemplate(core);

        return solrTemplate.queryForPage(new SimpleQuery(criteria), beanCls);
    }
}
