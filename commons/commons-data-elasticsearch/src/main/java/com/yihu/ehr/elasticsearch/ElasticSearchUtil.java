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
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;

/**
 * Util - Es搜索服务
 * Created by progr1mmer on 2017/12/2.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticSearchUtil {

    @Autowired
    private ElasticSearchPool elasticSearchPool;

    /**
     * 创建映射
     *  注意：保存数据之前如果没有创建相应的字
     *  段映射会导致搜索结果不准确
     * @param index
     * @param type
     * @param source
     * @param setting - 该设置根据需要进行配置
     * @throws IOException
     */
    public void mapping (String index, String type, Map<String, Map<String, String>> source, Map<String, Object> setting) throws IOException{
        TransportClient transportClient = elasticSearchPool.getClient();
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        for (String field : source.keySet()) {
            xContentBuilder.startObject(field);
            Map<String, String> propsMap = source.get(field);
            for (String prop : propsMap.keySet()) {
                xContentBuilder.field(prop, propsMap.get(prop));
            }
            xContentBuilder.endObject();
        }
        xContentBuilder.endObject().endObject();
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

    /**
     * 移除索引 - 整个移除
     * @param index
     */
    public void remove (String index){
        TransportClient transportClient = elasticSearchPool.getClient();
        DeleteIndexRequestBuilder deleteIndexRequestBuilder = transportClient.admin().indices().prepareDelete(index);
        deleteIndexRequestBuilder.get();
    }

    /**
     * 添加数据
     * @param index
     * @param type
     * @param source
     * @return
     * @throws ParseException
     */
    public Map<String, Object> index (String index, String type, Map<String, Object> source) throws ParseException{
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

    /**
     * 批量添加数据 - 效率高
     * @param index
     * @param type
     * @param source
     * @throws ParseException
     */
    public void bulkIndex (String index, String type, List<Map<String, Object>> source) throws ParseException{
        if (source.size() > 0) {
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
    }

    /**
     * 删除数据
     * @param index
     * @param type
     * @param id
     */
    public void delete (String index, String type, String id) {
        TransportClient transportClient = elasticSearchPool.getClient();
        transportClient.prepareDelete(index, type, id).get();
    }

    /**
     * 批量删除数据
     * @param index
     * @param type
     * @param idArr
     */
    public void bulkDelete (String index, String type, String [] idArr) {
        if (idArr.length > 0) {
            TransportClient transportClient = elasticSearchPool.getClient();
            BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
            for (String id : idArr) {
                bulkRequestBuilder.add(transportClient.prepareDelete(index, type, id));
            }
            bulkRequestBuilder.get();
        }
    }

    /**
     * 根据字段批量删除数据
     * @param index
     * @param type
     * @param field
     * @param value
     */
    public void deleteByField(String index, String type, String field, Object value) {
        deleteByFilter(index, type, field + "=" + value);
    }

    /**
     * 根据条件批量删除数据
     * @param index
     * @param type
     * @param filters
     */
    public void deleteByFilter(String index, String type, String filters) {
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        deleteByFilter(index, type, queryBuilder);
    }

    /**
     * 根据条件批量删除数据
     * @param index
     * @param type
     * @param queryBuilder
     */
    public void deleteByFilter(String index, String type, QueryBuilder queryBuilder) {
        List<String> idList = getIds(index, type, queryBuilder);
        if (idList.size() > 0) {
            TransportClient transportClient = elasticSearchPool.getClient();
            String [] idArr = new String[idList.size()];
            idArr = idList.toArray(idArr);
            BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
            for (String id : idArr) {
                bulkRequestBuilder.add(transportClient.prepareDelete(index, type, id));
            }
            bulkRequestBuilder.get();
        }
    }

    /**
     * 更新数据 - 返回最新文档
     * @param index
     * @param type
     * @param id
     * @param source
     * @return
     * @throws DocumentMissingException
     */
    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = elasticSearchPool.getClient();
        source.remove("_id");
        transportClient.prepareUpdate(index, type, id).setDoc(source).setRetryOnConflict(5).get();
        return findById(index, type, id);
    }

    /**
     * 更新数据 - 不返回文档
     * @param index
     * @param type
     * @param id
     * @param source
     * @throws DocumentMissingException
     */
    public void voidUpdate (String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = elasticSearchPool.getClient();
        source.remove("_id");
        transportClient.prepareUpdate(index, type, id).setDoc(source).setRetryOnConflict(5).get();
    }

    /**
     * 批量更新数据
     * @param index
     * @param type
     * @param source
     * @throws DocumentMissingException
     */
    public void bulkUpdate(String index, String type, List<Map<String, Object>> source) throws DocumentMissingException {
        if (source.size() > 0) {
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
    }

    /**
     * 根据ID查找数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    public Map<String, Object> findById(String index, String type, String id) {
        TransportClient transportClient = elasticSearchPool.getClient();
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse response = transportClient.get(getRequest).actionGet();
        Map<String, Object> source = response.getSource();
        if (source != null) {
            source.put("_id", response.getId());
        }
        return source;
    }

    /**
     * 根据字段查找数据
     * @param index
     * @param type
     * @param field
     * @param value
     * @return
     */
    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        return list(index, type, field + "=" + value);
    }

    /**
     * 获取文档列表
     * @param index
     * @param type
     * @param filters
     * @return
     */
    public List<Map<String, Object>> list(String index, String type, String filters) {
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        return list(index, type, queryBuilder);
    }

    /**
     * 获取文档列表
     * @param index
     * @param type
     * @param queryBuilder
     * @return
     */
    public List<Map<String, Object>> list(String index, String type, QueryBuilder queryBuilder) {
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

    /**
     * 获取文档分页
     * @param index
     * @param type
     * @param filters
     * @param page
     * @param size
     * @return
     */
    public List<Map<String, Object>> page(String index, String type, String filters, int page, int size) {
        return page(index, type, filters, null, page, size);
    }

    /**
     * 获取分档分页 - 带分页功能
     * @param index
     * @param type
     * @param filters
     * @param sorts
     * @param page
     * @param size
     * @return
     */
    public List<Map<String, Object>> page(String index, String type, String filters, String sorts, int page, int size) {
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        List<SortBuilder> sortBuilders = getSortBuilder(sorts);
        return page(index, type, queryBuilder, sortBuilders, page, size);
    }

    /**
     * 获取分档分页 - 带分页功能
     * @param index
     * @param type
     * @param queryBuilder
     * @param sortBuilders
     * @param page
     * @param size
     * @return
     */
    public List<Map<String, Object>> page(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, int page, int size) {
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

    /**
     * 获取文档数
     * @param index
     * @param type
     * @param filters
     * @return
     */
    public long count(String index, String type, String filters) {
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        return count(index, type, queryBuilder);
    }

    /**
     * 获取文档数
     * @param index
     * @param type
     * @param queryBuilder
     * @return
     */
    public long count(String index, String type, QueryBuilder queryBuilder) {
        TransportClient transportClient = elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        return builder.get().getHits().totalHits();
    }

    /**
     * 获取ID列表
     * @param index
     * @param type
     * @param filters
     * @return
     */
    public List<String> getIds (String index, String type, String filters){
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        return getIds(index, type, queryBuilder);
    }

    /**
     * 获取ID列表
     * @param index
     * @param type
     * @param queryBuilder
     * @return
     */
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

    /**
     * 根据SQL查找数据
     * @param field
     * @param sql
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> findBySql(List<String> field, String sql) throws Exception {
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
                for (String _field : field) {
                    rowData.put(_field, resultSet.getObject(_field));
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

    /**
     * 根据SQL查找数据
     * @param sql
     * @return
     * @throws Exception
     */
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

    /**
     * 根据日期分组
     * @param index
     * @param type
     * @param filters
     * @param start
     * @param end
     * @param field
     * @param interval
     * @param format
     * @return
     */
    public Map<String, Long> dateHistogram(String index, String type, String filters, Date start, Date end, String field, DateHistogramInterval interval, String format) {
        TransportClient transportClient = elasticSearchPool.getClient();
        QueryBuilder queryBuilder = getQueryBuilder(filters);
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
     * @param filters
     * @param filed
     * @return
     */
    public int cardinality(String index, String type, String filters, String filed){
        QueryBuilder queryBuilder = getQueryBuilder(filters);
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

    /**
     * 分组统计
     * @param index
     * @param type
     * @param filters
     * @param groupField
     * @return
     */
    public Map<String,Long> countByGroup(String index, String type, String filters,String groupField) {
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        Map<String,Long> groupMap = new HashMap<>();
        TransportClient transportClient = elasticSearchPool.getClient();
        AbstractAggregationBuilder aggregation = AggregationBuilders.terms("count").field(groupField);
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.addAggregation(aggregation);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        SearchResponse response = builder.get();
        Terms terms = response.getAggregations().get("count");
        List<Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            //System.out.println(bucket.getKey()+"----"+bucket.getDocCount());
            groupMap.put(bucket.getKey().toString(),bucket.getDocCount());
        }
        return groupMap;
    }

    /**
     * 分组求和
     * @param index
     * @param type
     * @param filters
     * @param sumField
     * @param groupField
     * @return
     */
    public Map<String,Double> sumtByGroup(String index, String type, String filters, String sumField, String groupField) {
        TransportClient transportClient = elasticSearchPool.getClient();
        QueryBuilder queryBuilder = getQueryBuilder(filters);
        Map<String,Double> groupMap = new HashMap<>();
        TermsBuilder aggregation = AggregationBuilders.terms("sum_query").field(groupField);
        SearchRequestBuilder builder = transportClient.prepareSearch(index);
        builder.setTypes(type);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        SumBuilder sumBuilder= AggregationBuilders.sum("sum_row").field(sumField);
        aggregation.subAggregation(sumBuilder);
        builder.addAggregation(aggregation);
        SearchResponse response = builder.get();
        Terms terms = response.getAggregations().get("sum_query");
        List<Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets){
            Sum sum2 = bucket.getAggregations().get("sum_row");
            groupMap.put(bucket.getKey().toString(),sum2.getValue());
        }
        return groupMap;
    }

    /**
     * 排序语句转换
     * @param sorts
     * @return
     */
    private List<SortBuilder> getSortBuilder(String sorts) {
        List<SortBuilder> sortBuilderList = new ArrayList<>();
        if (StringUtils.isEmpty(sorts)) {
            return sortBuilderList;
        }
        String [] sortArr = sorts.split(";");
        for (String sort : sortArr) {
            String operator = sort.substring(0, 1);
            SortBuilder sortBuilder = new FieldSortBuilder(sort.substring(1));
            if ("-".equalsIgnoreCase(operator.trim())) {
                sortBuilder.order(SortOrder.DESC);
            } else if ("+".equalsIgnoreCase(operator.trim())) {
                sortBuilder.order(SortOrder.ASC);
            } else {
                sortBuilder.order(SortOrder.DESC);
            }
            sortBuilderList.add(sortBuilder);
        }
        return sortBuilderList;
    }

    /**
     * 查询语句转换
     * @param filters
     * @return
     */
    public QueryBuilder getQueryBuilder(String filters) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isEmpty(filters)) {
            return boolQueryBuilder;
        }
        String [] filterArr = filters.split(";");
        for (String filter : filterArr) {
            if (filter.contains("||")){
                String [] fields = filter.split("\\|\\|");
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                for (String filed : fields) {
                    String [] condition = filed.split("=");
                    queryBuilder.should(QueryBuilders.termQuery(condition[0], condition[1]));
                }
                boolQueryBuilder.must(queryBuilder);
            } else if (filter.contains("?")) {
                String [] condition = filter.split("\\?");
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(condition[0], condition[1]);
                boolQueryBuilder.must(matchQueryBuilder);
            } else if (filter.contains("<>")) {
                String [] condition = filter.split("<>");
                if (condition[1].contains(",")) {
                    String [] inCondition = condition[1].split(",");
                    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(condition[0], inCondition);
                    boolQueryBuilder.mustNot(termsQueryBuilder);
                } else {
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(condition[0], condition[1]);
                    boolQueryBuilder.mustNot(termQueryBuilder);
                }
            } else if (filter.contains(">=")) {
                String [] condition = filter.split(">=");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.gte(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains(">")) {
                String [] condition = filter.split(">");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.gt(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("<=")) {
                String [] condition = filter.split("<=");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.lte(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("<")) {
                String [] condition = filter.split("<");
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(condition[0]);
                rangeQueryBuilder.lt(condition[1]);
                boolQueryBuilder.must(rangeQueryBuilder);
            } else if (filter.contains("=")) {
                String [] condition = filter.split("=");
                if (condition[1].contains(",")) {
                    String [] inCondition = condition[1].split(",");
                    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(condition[0], inCondition);
                    boolQueryBuilder.must(termsQueryBuilder);
                } else {
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(condition[0], condition[1]);
                    boolQueryBuilder.must(termQueryBuilder);
                }
            }
        }
        return boolQueryBuilder;
    }

}
