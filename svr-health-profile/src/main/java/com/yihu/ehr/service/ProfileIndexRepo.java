package com.yihu.ehr.service;

import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 健康档案Solr查询器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 17:56
 */
@Component
public class ProfileIndexRepo<SolrDocument, String> extends SimpleSolrRepository {
    public List<String> find(String query){

        return null;
    }
}
