package com.yihu.quota.etl.extract;

import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.util.*;

/**
 * Created by janseny on 2017/7/21.
 */
@Component
public class ElasticsearchUtil {

    @Autowired
    private ElasticFactory elasticFactory;

    private Client client;

    public Client getClient(String host,String clusterName){
        return this.client = elasticFactory.getClient(host, 9300, clusterName);
    }

    /**
     * @param esConfig 基本配置
     * @param boolQueryBuilder  查询参数 build
     * @param pageNo
     * @param pageSize
     * @param sortName 排序字段名称
     * @return
     */
    public List<Map<String, Object>> queryPageList(EsConfig esConfig,BoolQueryBuilder boolQueryBuilder,int pageNo,int pageSize,String sortName){
        getClient(esConfig.getHost(),esConfig.getClusterName());
        SearchResponse actionGet = null;
        SortBuilder dealSorter = SortBuilders.fieldSort(sortName).order(SortOrder.DESC);
        actionGet = client.prepareSearch(esConfig.getIndex())
                .setTypes(esConfig.getType())
                .setQuery(boolQueryBuilder)
                .setFrom(pageNo).setSize(pageSize).addSort(dealSorter)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
        }
        return matchRsult;
    }

    /**
     * @param esConfig 基本配置
     * @param boolQueryBuilder  查询参数 build
     * @return
     */
    public long getTotalCount(EsConfig esConfig,BoolQueryBuilder boolQueryBuilder){
        getClient(esConfig.getHost(),esConfig.getClusterName());
        SearchResponse actionGet = null;
        actionGet = client.prepareSearch(esConfig.getIndex())
                .setTypes(esConfig.getType())
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
       if(hits != null){
           return hits.totalHits();
       }
        return 0;
    }

    /**
     * @param esConfig 基本配置
     * @param boolQueryBuilder  查询参数 build
     * @param sortName 排序字段名称
     * @return
     */
    public List<Map<String, Object>> queryList(EsConfig esConfig,BoolQueryBuilder boolQueryBuilder,String sortName){
        getClient(esConfig.getHost(),esConfig.getClusterName());
        SearchResponse actionGet = null;
        SortBuilder dealSorter = SortBuilders.fieldSort(sortName).order(SortOrder.DESC);
        actionGet = client.prepareSearch(esConfig.getIndex())
                .setTypes(esConfig.getType())
                .setQuery(boolQueryBuilder)
                .addSort(dealSorter)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
        }
        return matchRsult;
    }


    /**
     * 执行搜索（带分组）
     * @param indexName 索引名称
     * @param typeName 类型名称
     * @param queryBuilder 查询内容
     * @param aggsField 要分组的字段
     * @return
     */
    public Map<String, Object> searcherByGroup(String indexName, String typeName,String host, String clusterName,
                                               BoolQueryBuilder queryBuilder, String aggsField) {
        getClient(host,clusterName);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(typeName).setQuery(queryBuilder);

        //创建TermsBuilder对象，使用term查询，设置该分组的名称为 name_count，并根据aggsField字段进行分组
        TermsBuilder termsBuilder = AggregationBuilders.terms(aggsField +"_count").field(aggsField);//此处也可继续设置order、size等
        //添加分组信息
        searchRequestBuilder.addAggregation(termsBuilder);
        //执行搜索
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        //解析返回数据，获取分组名称为aggs-class的数据
        Terms terms = searchResponse.getAggregations().get(aggsField +"_count");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKey().toString();
            dataMap.put(key, bucket.getDocCount() + "");
        }
        return dataMap;
    }

}
