package com.yihu.ehr.elasticsearch;

import com.alibaba.druid.pool.*;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Client - Es搜索服务
 * Created by progr1mmer on 2017/12/1.
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticSearchClient {

    @Autowired
    private ElasticSearchPool elasticSearchPool;

    public void mapping(String index, String type, XContentBuilder xContentBuilder) {
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            CreateIndexRequestBuilder createIndexRequestBuilder = transportClient.admin().indices().prepareCreate(index);
            createIndexRequestBuilder.addMapping(type, xContentBuilder);
            createIndexRequestBuilder.get();
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public void remove(String index) {
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            DeleteIndexRequestBuilder deleteIndexRequestBuilder = transportClient.admin().indices().prepareDelete(index);
            deleteIndexRequestBuilder.get();
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public Map<String, Object> index(String index, String type, Map<String, Object> source) {
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            if (StringUtils.isEmpty(source.get("_id"))) {
                IndexResponse response = transportClient.prepareIndex(index, type).setSource(source).get();
                source.put("_id", response.getId());
            } else {
                String _id = String.valueOf(source.get("_id"));
                source.remove("_id");
                IndexResponse response = transportClient.prepareIndex(index, type, _id).setSource(source).get();
                source.put("_id", response.getId());
            }
            return source;
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public void delete(String index, String type, String [] idArr) {
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            for (String id : idArr) {
                transportClient.prepareDelete(index, type, id).get();
            }
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            transportClient.prepareUpdate(index, type, id).setDoc(source).get();
            return findById(index, type, id);
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public Map<String, Object> findById(String index, String type, String id) {
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            GetRequest getRequest = new GetRequest(index, type, id);
            GetResponse response = transportClient.get(getRequest).actionGet();
            Map<String, Object> source = response.getSource();
            if (source != null) {
                source.put("_id", response.getId());
            }
            return source;
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public List<Map<String, Object>> findByField(String index, String type, QueryBuilder queryBuilder) {
        TransportClient transportClient = elasticSearchPool.getClient();
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
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public List<Map<String, Object>> page(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, int page, int size){
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            SearchRequestBuilder builder = transportClient.prepareSearch(index);
            sortBuilders.forEach(item -> builder.addSort(item));
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
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public List<String> getIds(String index, String type, QueryBuilder queryBuilder){
        TransportClient transportClient = elasticSearchPool.getClient();
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
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public long count(String index, String type, QueryBuilder queryBuilder){
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            SearchRequestBuilder builder = transportClient.prepareSearch(index);
            builder.setTypes(type);
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(queryBuilder);
            builder.setExplain(true);
            return builder.get().getHits().totalHits();
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }

    public List<Map<String, Object>> findBySql(List<String> fields, String sql) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        DruidDataSource druidDataSource = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            druidDataSource = elasticSearchPool.getDruidDataSource();
            connection = druidDataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (String field : fields) {
                    rowData.put(field, resultSet.getObject(field));
                }
                list.add(rowData);
            }
            return list;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (druidDataSource != null) {
                druidDataSource.close();
            }
        }
    }

    public ResultSet findBySql(String sql) throws Exception {
        DruidDataSource druidDataSource = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            druidDataSource = elasticSearchPool.getDruidDataSource();
            connection = druidDataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (druidDataSource != null) {
                druidDataSource.close();
            }
        }
    }

}
