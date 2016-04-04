package com.yihu.ehr.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Solr客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 17:56
 */
@Component
public class SolrClient {
    @Autowired
    SolrTemplate solrTemplate;

    public List<String> findRowKey(String tableName, String queryString, Map<String, String> sort, int pageSize, int page){
        return null;
    }
}
