package com.yihu.quota.etl.extract.es;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.extract.ElasticsearchUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.save.TjDataSave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.service.save.TjDataSaveService;
import net.sf.json.JSONObject;
import org.apache.solr.client.solrj.response.FacetField;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by janseny on 2017/6/24
 */
@Component
@Scope("prototype")
public class EsResultExtract {
    private Logger logger = LoggerFactory.getLogger(EsResultExtract.class);

    private String startTime;
    private String endTime;
    private String orgName;
    private String province;
    private String city;
    private String district;
    private TjQuota tjQuota;
    private String quotaCode;
    private int pageNo;
    private int pageSize;
    private EsConfig esConfig;
    @Autowired
    private ElasticFactory elasticFactory;
    @Autowired
    ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private TjDataSaveService tjDataSaveService;
    @Autowired
    private ObjectMapper objectMapper;

    public EsConfig getEsConfig(TjQuota tjQuota) throws Exception {
        //得到该指标的数据存储
        TjQuotaDataSave quotaDataSave = tjDataSaveService.findByQuota(tjQuota.getCode());
        //如果为空说明数据错误
        if (quotaDataSave == null) {
            throw new Exception("quotaDataSave data error");
        }
        //判断数据源是什么类型,根据类型和数据库相关的配置信息抽取数据
        EsConfig esConfig = null;
        if (TjDataSave.type_es.equals(quotaDataSave.getType())) {
            JSONObject obj = new JSONObject().fromObject(quotaDataSave.getConfigJson());
            esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
        }
        //初始化es链接
        esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(esConfig), EsConfig.class);
        return esConfig;
    }

    public void initialize(TjQuota tjQuota ,String filters) throws Exception {
        if(!StringUtils.isEmpty(filters)){
            Map<String, Object> params  = objectMapper.readValue(filters, new TypeReference<Map>() {});
            if (params !=null && params.size() > 0){
                for(String key : params.keySet()){
                    if( params.get(key) != null ){
                        if(key.equals("startTime"))
                            this.startTime = params.get(key).toString();
                        if(key.equals("endTime"))
                            this.endTime = params.get(key).toString();
                        if(key.equals("orgName"))
                            this.orgName = params.get(key).toString();
                        if(key.equals("province"))
                            this.province = params.get(key).toString();
                        if(key.equals("city"))
                            this.city = params.get(key).toString();
                        if(key.equals("district"))
                            this.district = params.get(key).toString();
                    }
                }
            }
        }
        this.tjQuota = tjQuota;
        if(tjQuota.getCode() != null)
            this.quotaCode = tjQuota.getCode();
        EsConfig esConfig = null;
        esConfig = getEsConfig(tjQuota);
        this.esConfig = esConfig;
    }

    public List<Map<String, Object>> queryResultPage(TjQuota tjQuota ,String filters,int pageNo,int pageSize) throws Exception {
        initialize(tjQuota,filters);
        this.pageNo = pageNo-1;
        this.pageSize = pageSize;
        List<Map<String, Object>> restltList = queryPageList(esConfig);
        for(Map<String,Object> map : restltList){
            System.out.println(map);
        }
        return restltList;
    }

    private List<Map<String, Object>> queryPageList(EsConfig esConfig){
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        return elasticsearchUtil.queryPageList(esConfig,boolQueryBuilder,pageNo,pageSize,"quotaDate");
    }

    public int getQuotaTotalCount(TjQuota tjQuota,String filters) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        return (int)elasticsearchUtil.getTotalCount(esConfig, boolQueryBuilder);
    }

    public BoolQueryBuilder getBoolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
        RangeQueryBuilder rangeQueryResult = QueryBuilders.rangeQuery("result").gte("0");
        boolQueryBuilder.must(rangeQueryResult);
        if( !StringUtils.isEmpty(quotaCode)){
            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery("quotaCode", quotaCode);
            boolQueryBuilder.must(termQueryQuotaCode);
        }
        if( !StringUtils.isEmpty(orgName) ){
            TermQueryBuilder termQueryOrgName = QueryBuilders.termQuery("orgName", orgName);
            boolQueryBuilder.must(termQueryOrgName);
        }
        if( !StringUtils.isEmpty(province) ){
            TermQueryBuilder termQueryProvince = QueryBuilders.termQuery("provinceName", province);
            boolQueryBuilder.must(termQueryProvince);
        }
        if( !StringUtils.isEmpty(city) ){
            TermQueryBuilder termQueryCity = QueryBuilders.termQuery("cityName", city);
            boolQueryBuilder.must(termQueryCity);
        }
        if( !StringUtils.isEmpty(district) ){
            TermQueryBuilder termQueryTown = QueryBuilders.termQuery("townName", district);
            boolQueryBuilder.must(termQueryTown);
        }
        if( !StringUtils.isEmpty(startTime) ){
            RangeQueryBuilder rangeQueryStartTime = QueryBuilders.rangeQuery("createTime").gte(startTime);
            boolQueryBuilder.must(rangeQueryStartTime);
        }
        if( !StringUtils.isEmpty(endTime)){
            RangeQueryBuilder rangeQueryEndTime = QueryBuilders.rangeQuery("createTime").lte(endTime);
            boolQueryBuilder.must(rangeQueryEndTime);
        }
        return boolQueryBuilder;
    }

    public List<Map<String, Object>> getQuotaReport(TjQuota tjQuota, String filters) throws Exception {
        initialize(tjQuota,filters);
        EsConfig esConfig = getEsConfig(tjQuota);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        List<Map<String, Object>> list = elasticsearchUtil.queryList(esConfig, boolQueryBuilder, "quotaDate");
        return  list;
    }

//    public Map<String, Integer> esClientQuery(String sql, Client client,String type){
//        try {
//            SQLExprParser parser = new ElasticSqlExprParser(sql);
//            SQLExpr expr = parser.expr();
//            if (parser.getLexer().token() != Token.EOF) {
//                throw new ParserException("illegal sql expr : " + sql);
//            }
//            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;
//            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
//            Select select = null;
//            select = new SqlParser().parseSelect(queryExpr);
//
//            AggregationQueryAction action = null;
//            DefaultQueryAction queryAction = null;
//            SqlElasticSearchRequestBuilder requestBuilder = null;
//            if (select.isAgg) {
//                //包含计算的的排序分组的
//                action = new AggregationQueryAction(client, select);
//                requestBuilder = action.explain();
//            } else {
//                //封装成自己的Select对象
//                queryAction = new DefaultQueryAction(client, select);
//                requestBuilder = queryAction.explain();
//            }
//            //之后就是对ES的操作
//            Iterator<Terms.Bucket> gradeBucketIt = null;
//            SearchResponse response = (SearchResponse) requestBuilder.get();
//            if(response.getAggregations().asList().get(0) instanceof LongTerms){
//                LongTerms longTerms = (LongTerms) response.getAggregations().asList().get(0);
//                gradeBucketIt = longTerms.getBuckets().iterator();
//            }else  if(response.getAggregations().asList().get(0) instanceof StringTerms){
//                StringTerms stringTerms = (StringTerms) response.getAggregations().asList().get(0);
//                gradeBucketIt = stringTerms.getBuckets().iterator();
//            }
//            //里面存放的数据 例  350200-5-2-2    主维度  细维度1  细维度2  值
//            Map<String,Integer> map = new HashMap<>();
//            //递归解析json
//            expainJson(gradeBucketIt, map, null);
//
//            if(type.equals("date")){
//                return computeTime(map);
//            }else {
//                return compute(map);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private Map<String, Integer> computeTime( Map<String, Integer> map) {
//        Map<String, Integer> result = new HashMap<>();
//        for (String key : map.keySet()){
//            Long time = Long.valueOf(key.substring(1));
//            Date date = DateUtil.toDateFromTime(time);
//            result.put(DateUtil.formatDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT),map.get(key));
//        }
//        return result;
//    }
//
//    private Map<String, Integer> compute( Map<String, Integer> map) {
//        Map<String, Integer> result = new HashMap<>();
//        for (String key : map.keySet()){
//            result.put(key.substring(1),map.get(key));
//        }
//        return result;
//    }


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
            }else {
                if (b.getAggregations().asList().get(0) instanceof InternalValueCount) {
                    InternalValueCount count = (InternalValueCount) b.getAggregations().asList().get(0);
                    map.put(new StringBuffer((sb == null ? "" : (sb.toString() + "-"))+ b.getKey()).toString() , (int)count.getValue());
                }else if (b.getAggregations().asList().get(0) instanceof InternalSum) {
                    InternalSum count = (InternalSum) b.getAggregations().asList().get(0);
                    map.put(new StringBuffer((sb == null ? "" : (sb.toString() + "-")) + "-" + b.getKey()).toString() , (int)count.getValue());
                }
            }
        }
    }

}
