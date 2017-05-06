package com.yihu.ehr.solr;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Solr底层操作类
 *
 * @author hzp
 * @version 1.0
 * @created 2017.05.06
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SolrAdmin {
    @Autowired
    SolrPool pool;


    /************************* 基础操作 **************************************************/

    /**
     * 新建单条索引
     */
    public SolrInputDocument create(String core,Map<String, Object> map) throws Exception {
        SolrClient client = pool.getConnection(core);
        SolrInputDocument doc = new SolrInputDocument();
        //注意date的格式，要进行适当的转化
        for(String key:map.keySet())
        {
            doc.addField(key, map.get(key));
        }
        client.add(doc);
        return doc;
    }
/*
    *//**
     * 修改单条索引
     *//*
    public SolrDocumentList query(String core,Map<String, Object> map) throws Exception {
        SolrClient client = pool.getConnection(core);
        SolrInputDocument doc = new SolrInputDocument();
        //注意date的格式，要进行适当的转化
        for(String key:map.keySet())
        {
            doc.addField(key, map.get(key));
        }
        client.add(doc);
        return doc;
    }*/
}
