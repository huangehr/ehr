package com.yihu.ehr.dfs.es.dao;

import com.yihu.ehr.dfs.es.config.ElasticSearchConfig;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2017/12/1.
 */
@Repository
public class ElasticSearchDao {

    private List<TransportClient> clientPool;
    private static String TYPE = "info";

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    @PostConstruct
    private void init() {
        clientPool = new ArrayList<TransportClient>();
        for(int size = 0; size < 5; size ++) {
            Settings settings = Settings.builder()
                    .put("cluster.name", elasticSearchConfig.getClusterName())
                    .put("client.transport.sniff", true)
                    .build();
            String[] nodeArr = elasticSearchConfig.getClusterNodes().split(",");
            InetSocketTransportAddress[] socketArr = new InetSocketTransportAddress[nodeArr.length];
            for (int i = 0; i < socketArr.length; i++) {
                if(!StringUtils.isEmpty(nodeArr[i])) {
                    String[] nodeInfo = nodeArr[i].split(":");
                    socketArr[i] = new InetSocketTransportAddress(new InetSocketAddress(nodeInfo[0], new Integer(nodeInfo[1])));
                }
            }
            TransportClient transportClient = TransportClient.builder().settings(settings).build().addTransportAddresses(socketArr);
            clientPool.add(transportClient);
        }
    }

    @PreDestroy
    private void destroy(){
        for(TransportClient transportClient : clientPool) {
            transportClient.close();
        }
    }

    public synchronized TransportClient getClient() {
        if(clientPool.size() > 0) {
            TransportClient transportClient = clientPool.get(0);
            clientPool.remove(0);
            return transportClient;
        }else {
            init();
            TransportClient transportClient = clientPool.get(0);
            clientPool.remove(0);
            return transportClient;
        }
    }

    public synchronized void releaseClient(TransportClient transportClient) {
        if(clientPool.size() > 10 ) {
            transportClient.close();
        }else {
            clientPool.add(transportClient);
        }
    }

    public void create(String index, XContentBuilder xContentBuilder) {
        TransportClient transportClient = getClient();
        CreateIndexRequestBuilder createIndexRequestBuilder = transportClient.admin().indices().prepareCreate(index);
        createIndexRequestBuilder.addMapping(TYPE, xContentBuilder);
        createIndexRequestBuilder.get();
        releaseClient(transportClient);
    }

    public void remove(String index) {
        TransportClient transportClient = getClient();
        DeleteIndexRequestBuilder deleteIndexRequestBuilder = transportClient.admin().indices().prepareDelete(index);
        deleteIndexRequestBuilder.get();
        releaseClient(transportClient);
    }

    public Map<String, Object> index(String index, Map<String, Object> source) {
        TransportClient transportClient = getClient();
        IndexResponse response = transportClient.prepareIndex(index, TYPE).setSource(source).get();
        releaseClient(transportClient);
        source.put("_id", response.getId());
        return source;
    }

    public void delete(String index, String [] idArr) {
        TransportClient transportClient = getClient();
        for(String id : idArr) {
            transportClient.prepareDelete(index, TYPE, id).get();
        }
        releaseClient(transportClient);
    }

    public Map<String, Object> update(String index, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = getClient();
        transportClient.prepareUpdate(index, TYPE, id).setDoc(source).get();
        releaseClient(transportClient);
        return findById(index, id);
    }

    public Map<String, Object> findById(String index, String id) {
        GetRequest getRequest = new GetRequest(index, TYPE, id);
        TransportClient transportClient = getClient();
        GetResponse response = transportClient.get(getRequest).actionGet();
        releaseClient(transportClient);
        Map<String, Object> source = response.getSource();
        if(source != null) {
            source.put("_id", response.getId());
        }
        return source;
    }

    public List<Map<String, Object>> findByField(String index, QueryBuilder queryBuilder) {
        TransportClient transportClient = getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(TYPE);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        SearchResponse response = builder.get();
        releaseClient(transportClient);
        SearchHits hits = response.getHits();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : hits.getHits()) {
            Map<String, Object> source = hit.getSource();
            source.put("_id", hit.getId());
            resultList.add(source);
        }
        return resultList;
    }

    public List<Map<String, Object>> page(String index, QueryBuilder queryBuilder, int page, int size){
        TransportClient transportClient = getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(TYPE);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setFrom((page - 1) * size).setSize(size).setExplain(true);
        SearchResponse response = builder.get();
        releaseClient(transportClient);
        SearchHits hits = response.getHits();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : hits.getHits()) {
            Map<String, Object> source = hit.getSource();
            source.put("_id", hit.getId());
            resultList.add(source);
        }
        return resultList;
    }
}
