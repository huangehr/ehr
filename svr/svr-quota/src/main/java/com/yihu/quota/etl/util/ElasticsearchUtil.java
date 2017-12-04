package com.yihu.quota.etl.util;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.parse.SqlParser;
import org.nlpcn.es4sql.query.AggregationQueryAction;
import org.nlpcn.es4sql.query.DefaultQueryAction;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by janseny on 2017/7/21.
 */
@Component
public class ElasticsearchUtil {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private EsConfigUtil esConfigUtil;


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
        actionGet = client.prepareSearch(esConfigUtil.getIndex())
                .setTypes(esConfigUtil.getType())
                .setQuery(boolQueryBuilder)
                .setFrom(pageNo - 1).setSize(pageSize).addSort(dealSorter)//从0开始算
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
        }
        return matchRsult;
    }

    /**
     * @param boolQueryBuilder  查询参数 build
     * @return
     */
    public long getTotalCount(Client client,BoolQueryBuilder boolQueryBuilder){
        SearchResponse actionGet = null;
        actionGet = client.prepareSearch(esConfigUtil.getIndex())
                .setTypes(esConfigUtil.getType())
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
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
    public List<Map<String, Object>> queryList(Client client,BoolQueryBuilder boolQueryBuilder,String sortName,int size){
        SearchResponse actionGet = null;
        SortBuilder dealSorter = null;
        if(sortName != null){
            dealSorter = SortBuilders.fieldSort(sortName).order(SortOrder.DESC);
        }else{
            dealSorter = SortBuilders.fieldSort("_id").order(SortOrder.DESC);
        }
        actionGet = client.prepareSearch(esConfigUtil.getIndex())
                .setTypes(esConfigUtil.getType())
                .setSize(size)
                .setQuery(boolQueryBuilder)
                .addSort(dealSorter)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
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
     * 执行搜索（带分组求和sum）
     * @param queryBuilder 查询内容
     * @param aggsField 要分组的字段
     * @param sumField 要求和的字段  只支持一个字段
     * @return
     */
    public List<Map<String, Object>> searcherByGroup(Client client,BoolQueryBuilder queryBuilder, String aggsField , String sumField) {

        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequestBuilder searchRequestBuilder =
                client.prepareSearch(esConfigUtil.getIndex())
                .setTypes(esConfigUtil.getType())
                .setQuery(queryBuilder);

        //创建TermsBuilder对象，使用term查询，设置该分组的名称为 name_count，并根据aggsField字段进行分组
        TermsBuilder termsBuilder = AggregationBuilders.terms(aggsField+"_val").field(aggsField);
        SumBuilder ageAgg = AggregationBuilders.sum(sumField+"_count").field(sumField);
        searchRequestBuilder.addAggregation(termsBuilder.subAggregation(ageAgg));
        Map<String, Object> dataMap = new HashMap<String, Object>();
        //执行搜索
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        //解析返回数据，获取分组名称为aggs-class的数据
        Terms terms = searchResponse.getAggregations().get(aggsField+"_val");
        Collection<Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKey().toString();
            if (bucket.getAggregations().asList().get(0) instanceof InternalSum) {//(sum(xx))
                InternalSum count = (InternalSum) bucket.getAggregations().asList().get(0);
                dataMap.put(key, count.value());
            }
        }
        list.add(dataMap);
        return list;
    }


    /**
     * 根据mysql 语句进行分组求和查询
     * @param client
     * @param index 索引名称
     * @param aggsFields 分组字段
     * @param filter 条件
     * @param sumField  求和字段
     * @return
     */
    public Map<String, Integer> searcherByGroupBySql(Client client,String index, String aggsFields ,String filter , String sumField) {
        Map<String,Integer> map = new HashMap<>();

//       String mysql1 = "select org ,sum(result) from quota where quotaCode='depart_treat_count' group by org  ";
        StringBuffer mysql = new StringBuffer("select ");
        mysql.append(aggsFields)
             .append(" ,sum(").append(sumField).append(") ")
             .append(" from ").append(index)
             .append(" where " ).append(filter)
             .append(" group by ").append(aggsFields);
        try {
            SQLExprParser parser = new ElasticSqlExprParser(mysql.toString());
            SQLExpr expr = parser.expr();
            if (parser.getLexer().token() != Token.EOF) {
                throw new ParserException("illegal sql expr : " + mysql);
            }
            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;
            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
            Select select = null;
            select = new SqlParser().parseSelect(queryExpr);

            AggregationQueryAction action = null;
            DefaultQueryAction queryAction = null;
            SqlElasticSearchRequestBuilder requestBuilder = null;
            if (select.isAgg) {
                //包含计算的的排序分组的
                action = new AggregationQueryAction(client, select);
                requestBuilder = action.explain();
            } else {
                //封装成自己的Select对象
                queryAction = new DefaultQueryAction(client, select);
                requestBuilder = queryAction.explain();
            }
            //之后就是对ES的操作
            SearchResponse response = (SearchResponse) requestBuilder.get();
            StringTerms stringTerms = (StringTerms) response.getAggregations().asList().get(0);
            Iterator<Terms.Bucket> gradeBucketIt = stringTerms.getBuckets().iterator();
            client.close();
            //里面存放的数据 例  350200-5-2-2    主维度  细维度1  细维度2  值
            //递归解析json
            expainJson(gradeBucketIt, map, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     *
     * @param client
     * @param source 表字段组合json格式
     * @return
     * @throws JsonProcessingException
     */
    public boolean save(Client client,String source) throws JsonProcessingException {
        IndexResponse indexResponse = client
                .prepareIndex(esConfigUtil.getIndex(), esConfigUtil.getType(), null)
                .setSource(source).get();
        boolean result =  indexResponse.isCreated();
        return result;
    }

    public void deleteById(Client client ,String id){
        DeleteRequestBuilder drBuilder = client.prepareDelete(esConfigUtil.getIndex(), esConfigUtil.getType(), id);
        drBuilder.execute().actionGet();
    }

    /**
     * 查询后 存在 删除
     * @param boolQueryBuilder
     */
    public void queryDelete(Client client,BoolQueryBuilder boolQueryBuilder){
        SearchResponse actionGet = null;
        actionGet = client.prepareSearch(esConfigUtil.getIndex())
                .setTypes(esConfigUtil.getType())
                .setSize(10000)
                .setQuery(boolQueryBuilder)
                .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
            DeleteRequestBuilder drBuilder = client.prepareDelete(esConfigUtil.getIndex(), esConfigUtil.getType(), hit.getId());
            drBuilder.execute().actionGet();
        }
    }


    /**
     * 递归解析json
     *
     * @param gradeBucketIt
     * @param map
     * @param sb
     */
    private void expainJson(Iterator<Terms.Bucket> gradeBucketIt,Map<String,Integer>map, StringBuffer sb) {
        while (gradeBucketIt.hasNext()) {
            Terms.Bucket b =  gradeBucketIt.next();
            if (b.getAggregations().asList().get(0) instanceof StringTerms) {
                StringTerms stringTermsCh = (StringTerms) b.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketItCh = stringTermsCh.getBuckets().iterator();
                while (gradeBucketItCh.hasNext()) {
                    StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                    expainJson(gradeBucketItCh, map, sbTemp);
                }
            }else if (b.getAggregations().asList().get(0) instanceof LongTerms) {
                LongTerms longTermsCh = (LongTerms) b.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketItCh = longTermsCh.getBuckets().iterator();
                while (gradeBucketItCh.hasNext()) {
                    StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                    expainJson(gradeBucketItCh, map, sbTemp);
                }
            }else if (b.getAggregations().asList().get(0) instanceof DoubleTerms) {
                DoubleTerms doubleTermsCh = (DoubleTerms) b.getAggregations().asList().get(0);
                Iterator<Terms.Bucket> gradeBucketItCh = doubleTermsCh.getBuckets().iterator();
                while (gradeBucketItCh.hasNext()) {
                    StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                    expainJson(gradeBucketItCh, map, sbTemp);
                }
            }else if (b.getAggregations().asList().get(0) instanceof InternalValueCount) {//count(8)
                InternalValueCount count = (InternalValueCount) b.getAggregations().asList().get(0);
                StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                map.put(sbTemp.toString() , (int)count.getValue());
            }else if (b.getAggregations().asList().get(0) instanceof InternalSum) {//(sum(xx))
                InternalSum count = (InternalSum) b.getAggregations().asList().get(0);
                StringBuffer sbTemp = new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + b.getKey());
                map.put(sbTemp.toString() , (int)count.getValue());
            }
        }
    }

}
