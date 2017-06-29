package com.yihu.quota.etl.extract.es;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.save.TjDataSave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.service.save.TjDataSaveService;
import net.sf.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    private TjDataSaveService tjDataSaveService;
    @Autowired
    private ObjectMapper objectMapper;

    public List<Map<String, Object>> queryResultListBySql(TjQuota tjQuota ,String filters,int pageNo,int pageSize) throws Exception {
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
        this.tjQuota = tjQuota;
        if(tjQuota.getCode() != null)
            this.quotaCode = tjQuota.getCode();
        this.pageNo = pageNo;
        this.pageSize = pageSize;

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
        this.esConfig = esConfig;
        List<Map<String, Object>> restltList = query();
        for(Map<String,Object> map : restltList){
            System.out.println(map);
        }
        return restltList;
    }




    private List<Map<String, Object>> query(){
        Client client = elasticFactory.getClient(esConfig.getHost(), 9300, null);
       ////模糊查询
//        WildcardQueryBuilder queryBuilder1 = QueryBuilders.wildcardQuery( "name", "*jack*");//搜索名字中含有jack的文档

        SearchResponse actionGet = null;
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);

        actionGet = client.prepareSearch(esConfig.getIndex())
        .setTypes(esConfig.getType())
        .setQuery(boolQueryBuilder)
        .setFrom(pageNo).setSize(pageSize)
        .execute().actionGet();
        SearchHits hits = actionGet.getHits();
        List<Map<String, Object>> matchRsult = new LinkedList<Map<String, Object>>();
        for (SearchHit hit : hits.getHits()){
            matchRsult.add(hit.getSource());
        }
        return matchRsult;
    }

    public int getQuotaTotalCount(){
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        Client client = elasticFactory.getClient(esConfig.getHost(), 9300, null);
        long count = client.prepareCount(esConfig.getIndex()).setTypes(esConfig.getType()).setQuery(boolQueryBuilder).execute().actionGet().getCount();
        return (int)count;
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


}
