package com.yihu.ehr.profile.persist.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Repository;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 16:28
 */
public class XProfileIndicesRepoImpl implements XProfileIndicesRepoCustom {
    @Autowired
    SolrTemplate solrTemplate;

    private SolrTemplate getSolrTemplate(String core) {
        solrTemplate.setSolrCore(core);
        /*SolrTemplate solrTemplate = solrTemplateMap.get(core);
        if (null == solrTemplate) {
            solrTemplate = new SolrTemplate(new MulticoreSolrServerFactory(solrClient));
            solrTemplate.setSolrCore(core);
            solrTemplate.afterPropertiesSet();
            solrTemplateMap.put(core, solrTemplate);
        }*/

        return solrTemplate;
    }

    public <T> Page<T> query(String core, Criteria criteria, Class<T> beanCls) {
        SolrTemplate solrTemplate = getSolrTemplate(core);

        return solrTemplate.queryForPage(new SimpleQuery(criteria), beanCls);
    }
}
