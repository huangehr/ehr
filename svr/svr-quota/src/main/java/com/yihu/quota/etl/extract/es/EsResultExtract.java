package com.yihu.quota.etl.extract.es;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.etl.extract.ElasticsearchUtil;
import com.yihu.quota.etl.extract.EsClientUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.save.TjDataSave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.service.save.TjDataSaveService;
import net.sf.json.JSONObject;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
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
    private String org;
    private String orgName;
    private String province;
    private String city;
    private String district;
    private String slaveKey1;
    private String slaveKey2;
    private String result;
    private TjQuota tjQuota;
    private String quotaCode;
    private int pageNo;
    private int pageSize;
    private EsConfig esConfig;
    @Autowired
    private EsClientUtil esClientUtil;
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
                        else if(key.equals("endTime"))
                            this.endTime = params.get(key).toString();
                        else if(key.equals("orgName"))
                            this.orgName = params.get(key).toString();
                        else if(key.equals("org"))
                            this.org = params.get(key).toString();
                        else if(key.equals("province"))
                            this.province = params.get(key).toString();
                        else if(key.equals("city"))
                            this.city = params.get(key).toString();
                        else if(key.equals("district"))
                            this.district = params.get(key).toString();
                        else if(key.equals("slaveKey1"))
                            this.slaveKey1 = params.get(key).toString();
                        else if(key.equals("slaveKey2"))
                            this.slaveKey2 = params.get(key).toString();
                        else if(key.equals("result")){
                            this.result = params.get(key).toString();
                        }
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

    public Client getEsClient(EsConfig esConfig){
        esClientUtil.getConfig(esConfig);
        Client client = esClientUtil.getClient();
        return  client;
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
        return elasticsearchUtil.queryPageList(getEsClient(esConfig),boolQueryBuilder,pageNo,pageSize,"quotaDate");
    }

    public int getQuotaTotalCount(TjQuota tjQuota,String filters) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        return (int)elasticsearchUtil.getTotalCount(getEsClient(esConfig),boolQueryBuilder);
    }

    public BoolQueryBuilder getBoolQueryBuilder(BoolQueryBuilder boolQueryBuilder){

        if( !StringUtils.isEmpty(result)){
            RangeQueryBuilder rangeQueryResult = QueryBuilders.rangeQuery("result").gte(result);
            boolQueryBuilder.must(rangeQueryResult);
        }
        if( !StringUtils.isEmpty(quotaCode)){
            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery("quotaCode", quotaCode);
            boolQueryBuilder.must(termQueryQuotaCode);
        }
        if( !StringUtils.isEmpty(orgName) ){
            TermQueryBuilder termQueryOrgName = QueryBuilders.termQuery("orgName", orgName);
            boolQueryBuilder.must(termQueryOrgName);
        }
        if( !StringUtils.isEmpty(org) ){
            String [] orgvals =org.split(",");
            for(int i=0;i<orgvals.length ; i++){
                TermQueryBuilder termQueryOrg = QueryBuilders.termQuery("org", orgvals[i]);
                boolQueryBuilder.mustNot(termQueryOrg);
            }
        }
        if( !StringUtils.isEmpty(province) ){
            TermQueryBuilder termQueryProvince = QueryBuilders.termQuery("provinceName", province);
            boolQueryBuilder.must(termQueryProvince);
        }
        if( !StringUtils.isEmpty(slaveKey1) ){
            TermQueryBuilder termQueryProvince = QueryBuilders.termQuery("slaveKey1", province);
            boolQueryBuilder.must(termQueryProvince);
        }
        if( !StringUtils.isEmpty(slaveKey2) ){
            TermQueryBuilder termQueryProvince = QueryBuilders.termQuery("slaveKey2", province);
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
        List<Map<String, Object>> list = elasticsearchUtil.queryList(getEsClient(esConfig),boolQueryBuilder, "quotaDate");
        return  list;
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
