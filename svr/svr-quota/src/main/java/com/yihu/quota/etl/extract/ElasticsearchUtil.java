package com.yihu.quota.etl.extract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import net.sf.json.JSON;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
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
    ObjectMapper objectMapper;

    @Autowired
    private EsClientUtil esClientUtil;


    /**
     * @param boolQueryBuilder  查询参数 build
     * @param pageNo
     * @param pageSize
     * @param sortName 排序字段名称
     * @return
     */
    public List<Map<String, Object>> queryPageList( Client client,BoolQueryBuilder boolQueryBuilder,
                                                   int pageNo,int pageSize,String sortName){
        SearchResponse actionGet = null;
        SortBuilder dealSorter = SortBuilders.fieldSort(sortName).order(SortOrder.DESC);
        actionGet = client.prepareSearch(esClientUtil.getIndex())
                .setTypes(esClientUtil.getType())
                .setQuery(boolQueryBuilder)
                .setFrom(pageNo).setSize(pageSize).addSort(dealSorter)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
        }
        client.close();
        return matchRsult;
    }

    /**
     * @param boolQueryBuilder  查询参数 build
     * @return
     */
    public long getTotalCount(Client client,BoolQueryBuilder boolQueryBuilder){
        SearchResponse actionGet = null;
        actionGet = client.prepareSearch(esClientUtil.getIndex())
                .setTypes(esClientUtil.getType())
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        client.close();
       if(hits != null){
           return hits.totalHits();
       }
        return 0;
    }

    /**
     * @param boolQueryBuilder  查询参数 build
     * @param sortName 排序字段名称
     * @return
     */
    public List<Map<String, Object>> queryList(Client client,BoolQueryBuilder boolQueryBuilder,String sortName){
        SearchResponse actionGet = null;
        SortBuilder dealSorter = null;
        if(sortName != null){
            dealSorter = SortBuilders.fieldSort(sortName).order(SortOrder.DESC);
        }else{
            dealSorter = SortBuilders.fieldSort("_id").order(SortOrder.DESC);
        }
        actionGet = client.prepareSearch(esClientUtil.getIndex())
                .setTypes(esClientUtil.getType())
                .setQuery(boolQueryBuilder)
                .addSort(dealSorter)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        client.close();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            Map<String, Object> map = new HashMap<>() ;
            map = hit.getSource();
            map.put("id",hit.getId());
            matchRsult.add(map);
        }
        return matchRsult;
    }


    /**
     * 执行搜索（带分组）
     * @param queryBuilder 查询内容
     * @param aggsField 要分组的字段
     * @return
     */
    public Map<String, Object> searcherByGroup(Client client,BoolQueryBuilder queryBuilder, String aggsField) {
        SearchRequestBuilder searchRequestBuilder =
                client.prepareSearch(esClientUtil.getIndex())
                .setTypes(esClientUtil.getType())
                .setQuery(queryBuilder);

        //创建TermsBuilder对象，使用term查询，设置该分组的名称为 name_count，并根据aggsField字段进行分组
        TermsBuilder termsBuilder = AggregationBuilders.terms(aggsField +"_count").field(aggsField);//此处也可继续设置order、size等
        //添加分组信息
        searchRequestBuilder.addAggregation(termsBuilder);
        //执行搜索
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        //解析返回数据，获取分组名称为aggs-class的数据
        Terms terms = searchResponse.getAggregations().get(aggsField +"_count");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        client.close();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKey().toString();
            dataMap.put(key, bucket.getDocCount() + "");
        }
        return dataMap;
    }


    /**
     *
     * @param client
     * @param source 表字段组合json格式
     * @return
     * @throws JsonProcessingException
     */
    public boolean save(Client client,String source) throws JsonProcessingException {
//        String source = objectMapper.writeValueAsString(object);
        IndexResponse indexResponse = client
                .prepareIndex(esClientUtil.getIndex(), esClientUtil.getType(), null)
                .setSource(source).get();
        boolean result =  indexResponse.isCreated();
        client.close();
        return result;
    }

    public void deleteById(Client client ,String id){
//        DeleteResponse deleteresponse = client.prepareDelete(esClientUtil.getIndex(), esClientUtil.getType(), id)
//                .execute().actionGet();
        DeleteRequestBuilder drBuilder = client.prepareDelete(esClientUtil.getIndex(), esClientUtil.getType(), id);
        drBuilder.execute().actionGet();
        client.close();
    }

    /**
     * 查询后 存在 删除
     * @param boolQueryBuilder
     */
    public void queryDelete(Client client,BoolQueryBuilder boolQueryBuilder){
        SearchResponse actionGet = null;
        actionGet = client.prepareSearch(esClientUtil.getIndex())
                .setTypes(esClientUtil.getType())
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
            DeleteRequestBuilder drBuilder = client.prepareDelete(esClientUtil.getIndex(), esClientUtil.getType(), hit.getId());
            drBuilder.execute().actionGet();
        }
        client.close();
    }

}
