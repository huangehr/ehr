package com.yihu.ehr.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
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
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
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

    public void mapping(String index, String type, XContentBuilder xContentBuilder, Map<String, Object> setting) {
        TransportClient transportClient = elasticSearchPool.getClient();
        CreateIndexRequestBuilder createIndexRequestBuilder = transportClient.admin().indices().prepareCreate(index);
        createIndexRequestBuilder.addMapping(type, xContentBuilder);
        /*Map<String, Object> settingSource = new HashMap<>();
        settingSource.put("index.translog.flush_threshold_size", "1g"); //log文件大小
        settingSource.put("index.translog.flush_threshold_ops", "100000"); //flush触发次数
        settingSource.put("index.translog.durability", "async"); //异步更新
        settingSource.put("index.refresh_interval", "30s"); //刷新间隔
        settingSource.put("index.number_of_replicas", 1); //副本数
        settingSource.put("index.number_of_shards", 3); //分片数
        createIndexRequestBuilder.setSettings(settingSource);*/
        if (setting != null && !setting.isEmpty()) {
            createIndexRequestBuilder.setSettings(setting);
        }
        createIndexRequestBuilder.get();
    }

    public void remove(String index) {
        TransportClient transportClient = elasticSearchPool.getClient();
        DeleteIndexRequestBuilder deleteIndexRequestBuilder = transportClient.admin().indices().prepareDelete(index);
        deleteIndexRequestBuilder.get();
    }

    public Map<String, Object> index (String index, String type, Map<String, Object> source) {
        TransportClient transportClient = elasticSearchPool.getClient();
        String _id = (String) source.remove("_id");
        if (StringUtils.isEmpty(_id)) {
            IndexResponse response = transportClient.prepareIndex(index, type).setSource(source).get();
            source.put("_id", response.getId());
        } else {
            IndexResponse response = transportClient.prepareIndex(index, type, _id).setSource(source).get();
            source.put("_id", response.getId());
        }
        return source;
    }

    public void bulkIndex (String index, String type, List<Map<String, Object>> source) {
        TransportClient transportClient = elasticSearchPool.getClient();
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        source.forEach(item -> {
            String _id = (String) item.remove("_id");
            if (StringUtils.isEmpty(_id)) {
                bulkRequestBuilder.add(transportClient.prepareIndex(index, type).setSource(item));
            } else {
                bulkRequestBuilder.add(transportClient.prepareIndex(index, type, _id).setSource(item));
            }
        });
        bulkRequestBuilder.get();
    }

    public void delete(String index, String type, String id) {
        TransportClient transportClient = elasticSearchPool.getClient();
        transportClient.prepareDelete(index, type, id).get();
    }

    public void bulkDelete(String index, String type, String [] idArr) {
        TransportClient transportClient = elasticSearchPool.getClient();
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        for (String id : idArr) {
            bulkRequestBuilder.add(transportClient.prepareDelete(index, type, id));
        }
        bulkRequestBuilder.get();
    }

    public Map<String, Object> update (String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = elasticSearchPool.getClient();
        transportClient.prepareUpdate(index, type, id).setDoc(source).setRetryOnConflict(5).get();
        return findById(index, type, id);
    }

    public void voidUpdate (String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = elasticSearchPool.getClient();
        transportClient.prepareUpdate(index, type, id).setDoc(source).setRetryOnConflict(5).get();
    }

    public void bulkUpdate (String index, String type, List<Map<String, Object>> source) throws DocumentMissingException {
        TransportClient transportClient = elasticSearchPool.getClient();
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        source.forEach(item -> {
            String _id = (String)item.remove("_id");
            if (!StringUtils.isEmpty(_id)) {
                bulkRequestBuilder.add(transportClient.prepareUpdate(index, type, _id).setDoc(item).setRetryOnConflict(5));
            }
        });
        bulkRequestBuilder.get();
    }

    public Map<String, Object> findById (String index, String type, String id) {
        TransportClient transportClient = elasticSearchPool.getClient();
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse response = transportClient.get(getRequest).actionGet();
        Map<String, Object> source = response.getSource();
        if (source != null) {
            source.put("_id", response.getId());
        }
        return source;
    }

    public List<Map<String, Object>> findByField(String index, String type, QueryBuilder queryBuilder) {
        int size = (int)count(index, type, queryBuilder);
        TransportClient transportClient = elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setFrom(0).setSize(size);
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
    }

    public List<Map<String, Object>> page(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, int page, int size){
        TransportClient transportClient = elasticSearchPool.getClient();
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
    }

    public List<String> getIds (String index, String type, QueryBuilder queryBuilder){
        TransportClient transportClient = elasticSearchPool.getClient();
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
    }

    public long count (String index, String type, QueryBuilder queryBuilder){
        TransportClient transportClient = elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        return builder.get().getHits().totalHits();
    }

    public Map<String,Long> countByGroup (String index, String type, QueryBuilder queryBuilder,String groupField){
        Map<String,Long> groupMap = new HashMap<>();
        TransportClient transportClient = elasticSearchPool.getClient();
        AbstractAggregationBuilder aggregation = AggregationBuilders.terms("count").field(groupField);
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.addAggregation(aggregation);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        builder.get().getHits().totalHits();
        return groupMap;
    }

    public List<Map<String, Object>> findBySql (List<String> fields, String sql) throws Exception {
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
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())){
                e.printStackTrace();
            }
           return new ArrayList<>();
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

    public ResultSet findBySql (String sql) throws Exception {
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

    public Map<String, Long> dateHistogram (String index, String type, QueryBuilder queryBuilder, Date start, Date end, String field, DateHistogramInterval interval, String format) {
        TransportClient transportClient = elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder(index + "-" + field);
        dateHistogramBuilder.field(field);
        dateHistogramBuilder.interval(interval);
        if (!StringUtils.isEmpty(format)) {
            dateHistogramBuilder.format(format);
        }
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.extendedBounds(start.getTime(), end.getTime());
        builder.addAggregation(dateHistogramBuilder);
        builder.setSize(0);
        builder.setExplain(true);
        SearchResponse response = builder.get();
        Histogram histogram = response.getAggregations().get(index + "-" + field);
        Map<String, Long> temp = new HashMap<>();
        histogram.getBuckets().forEach(item -> temp.put(item.getKeyAsString(), item.getDocCount()));
        return temp;
    }

    /**
     * 查询去重数量
     * @param index
     * @param type
     * @param queryBuilder
     * @param filed
     * @return
     */
    public int cardinality (String index, String type, QueryBuilder queryBuilder, String  filed) {
        TransportClient transportClient = elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        CardinalityBuilder cardinality = AggregationBuilders.cardinality("cardinality").field(filed);
        builder.addAggregation(cardinality);
        builder.setSize(0);
        builder.setExplain(true);
        SearchResponse response = builder.get();
        InternalCardinality internalCard = response.getAggregations().get("cardinality");
        return new Double(internalCard.getProperty("value").toString()).intValue();
    }
}
