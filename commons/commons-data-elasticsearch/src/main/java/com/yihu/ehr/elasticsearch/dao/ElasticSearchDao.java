package com.yihu.ehr.elasticsearch.dao;

import com.yihu.ehr.elasticsearch.config.ElasticSearchConfig;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dao - Es搜索服务
 * Created by progr1mmer on 2017/12/1.
 */
@Repository
public class ElasticSearchDao {

    @Value("${elasticsearch.pool.init-size}")
    private int initSize;
    @Value("${elasticsearch.pool.max-size}")
    private int maxSize;
    private List<TransportClient> clientPool;

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    @PostConstruct
    private void init() {
        if(clientPool == null) {
            clientPool = new ArrayList<TransportClient>();
        }
        synchronized (ElasticSearchDao.class) {
            while (clientPool.size() < initSize) {
                Settings settings = Settings.builder()
                        .put("cluster.name", elasticSearchConfig.getClusterName())
                        .put("client.transport.sniff", true)
                        .build();
                String[] nodeArr = elasticSearchConfig.getClusterNodes().split(",");
                InetSocketTransportAddress[] socketArr = new InetSocketTransportAddress[nodeArr.length];
                for (int i = 0; i < socketArr.length; i++) {
                    if (!StringUtils.isEmpty(nodeArr[i])) {
                        String[] nodeInfo = nodeArr[i].split(":");
                        socketArr[i] = new InetSocketTransportAddress(new InetSocketAddress(nodeInfo[0], new Integer(nodeInfo[1])));
                    }
                }
                TransportClient transportClient = TransportClient.builder().settings(settings).build().addTransportAddresses(socketArr);
                clientPool.add(transportClient);
            }
        }
    }

    @PreDestroy
    private void destroy(){
        if (null != clientPool) {
            for (TransportClient transportClient : clientPool) {
                transportClient.close();
            }
        }
    }

    public synchronized TransportClient getClient() {
        int last_index = clientPool.size() - 1;
        TransportClient transportClient = clientPool.get(last_index);
        clientPool.remove(last_index);
        if(clientPool.isEmpty()) {
            init();
        }
        return transportClient;
    }

    public synchronized void releaseClient(TransportClient transportClient) {
        if(clientPool.size() > maxSize) {
            if (null != transportClient) {
                transportClient.close();
            }
        }else {
            clientPool.add(transportClient);
        }
    }

    public void mapping(String index, String type, XContentBuilder xContentBuilder) {
        TransportClient transportClient = getClient();
        try {
            CreateIndexRequestBuilder createIndexRequestBuilder = transportClient.admin().indices().prepareCreate(index);
            createIndexRequestBuilder.addMapping(type, xContentBuilder);
            createIndexRequestBuilder.get();
        }finally {
            releaseClient(transportClient);
        }
    }

    public void remove(String index) {
        TransportClient transportClient = getClient();
        try {
            DeleteIndexRequestBuilder deleteIndexRequestBuilder = transportClient.admin().indices().prepareDelete(index);
            deleteIndexRequestBuilder.get();
        }finally {
            releaseClient(transportClient);
        }
    }

    public Map<String, Object> index(String index, String type, Map<String, Object> source) {
        TransportClient transportClient = getClient();
        try {
            IndexResponse response = transportClient.prepareIndex(index, type).setSource(source).get();
            source.put("_id", response.getId());
            return source;
        }finally {
            releaseClient(transportClient);
        }
    }

    public void delete(String index, String type, String [] idArr) {
        TransportClient transportClient = getClient();
        try {
            for (String id : idArr) {
                transportClient.prepareDelete(index, type, id).get();
            }
        }finally {
            releaseClient(transportClient);
        }
    }

    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = getClient();
        try {
            transportClient.prepareUpdate(index, type, id).setDoc(source).get();
            return findById(index, type, id);
        }finally {
            releaseClient(transportClient);
        }
    }

    public Map<String, Object> findById(String index, String type, String id) {
        TransportClient transportClient = getClient();
        try {
            GetRequest getRequest = new GetRequest(index, type, id);
            GetResponse response = transportClient.get(getRequest).actionGet();
            Map<String, Object> source = response.getSource();
            if(source != null) {
                source.put("_id", response.getId());
            }
            return source;
        }finally {
            releaseClient(transportClient);
        }
    }

    public List<Map<String, Object>> findByField(String index, String type, QueryBuilder queryBuilder) {
        TransportClient transportClient = getClient();
        try {
            SearchRequestBuilder builder = transportClient.prepareSearch(index);
            builder.setTypes(type);
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(queryBuilder);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            SearchHits hits = response.getHits();
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (SearchHit hit : hits.getHits()) {
                Map<String, Object> source = hit.getSource();
                source.put("_id", hit.getId());
                resultList.add(source);
            }
            return resultList;
        }finally {
            releaseClient(transportClient);
        }
    }

    public List<Map<String, Object>> page(String index, String type, QueryBuilder queryBuilder, int page, int size){
        TransportClient transportClient = getClient();
        try {
            SearchRequestBuilder builder = transportClient.prepareSearch(index);
            builder.setTypes(type);
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(queryBuilder);
            builder.setFrom((page - 1) * size).setSize(size);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            SearchHits hits = response.getHits();
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (SearchHit hit : hits.getHits()) {
                Map<String, Object> source = hit.getSource();
                source.put("_id", hit.getId());
                resultList.add(source);
            }
            return resultList;
        }finally {
            releaseClient(transportClient);
        }
    }

    public List<String> getIds(String index, String type, QueryBuilder queryBuilder){
        TransportClient transportClient = getClient();
        try {
            SearchRequestBuilder builder = transportClient.prepareSearch(index);
            builder.setTypes(type);
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(queryBuilder);
            builder.setFrom(0).setSize(10000);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            SearchHits hits = response.getHits();
            List<String> resultList = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                resultList.add(hit.getId());
            }
            return resultList;
        }finally {
            releaseClient(transportClient);
        }
    }

    public long count(String index, String type, QueryBuilder queryBuilder){
        TransportClient transportClient = getClient();
        try {
            SearchRequestBuilder builder = transportClient.prepareSearch(index);
            builder.setTypes(type);
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(queryBuilder);
            builder.setExplain(true);
            return builder.get().getHits().totalHits();
        }finally {
            releaseClient(transportClient);
        }
    }

}
