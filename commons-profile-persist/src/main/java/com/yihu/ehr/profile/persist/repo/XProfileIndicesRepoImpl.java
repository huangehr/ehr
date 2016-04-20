package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.persist.ProfileIndices;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
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

    private final static String ProfileCore = "HealthArchives";

    private Map<String, SolrTemplate> solrTemplateMap = new HashMap<>();

    public XProfileIndicesRepoImpl(){
    }

    public Page<ProfileIndices> find(Query query, Pageable pageable) {
        query.setPageRequest(pageable);
        Page<ProfileIndices> result = getSolrTemplate(ProfileCore).queryForPage(query, ProfileIndices.class);

        return result;
    }

    SolrTemplate getSolrTemplate(String core){
        SolrTemplate solrTemplate = solrTemplateMap.get(core);
        if(null == solrTemplate){
            solrTemplate = new SolrTemplate(new MulticoreSolrServerFactory(cloudSolrServer));
            solrTemplate.setSolrCore(core);
            solrTemplate.afterPropertiesSet();
            solrTemplateMap.put(core, solrTemplate);
        }

        return solrTemplate;
    }
}
