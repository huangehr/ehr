package com.yihu.quota.etl.extract.es;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.etl.util.EsConfigUtil;
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
    private String town;
    private String slaveKey1;
    private String slaveKey2;
    private String result;
    private TjQuota tjQuota;
    private String quotaCode;
    private int pageNo;
    private int pageSize;
    private EsConfig esConfig;
    @Autowired
    ElasticsearchUtil elasticsearchUtil;
    @Autowired
    EsConfigUtil esConfigUtil;
    @Autowired
    EsClientUtil esClientUtil;
    @Autowired
    private TjDataSaveService tjDataSaveService;
    @Autowired
    private ObjectMapper objectMapper;

    public void initialize(TjQuota tjQuota ,String filters) throws Exception {
        this.startTime = null;
        this.endTime = null;
        this.orgName = null;
        this.org = null;
        this.province = null;
        this.city = null;
        this.town = null;
        this.quotaCode = null;
        this.result = null;
        this.slaveKey1 = null;
        this.slaveKey2 = null;

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
                        else if(key.equals("town"))
                            this.town = params.get(key).toString();
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
        }else {
            // wait TO DO
        }
        //初始化es链接
        esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(esConfig), EsConfig.class);
        return esConfig;
    }

    public Client getEsClient(){
       return esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(),esConfig.getIndex(),esConfig.getType(), esConfig.getClusterName());
    }

    public List<Map<String, Object>> queryResultPage(TjQuota tjQuota ,String filters,int pageNo,int pageSize) throws Exception {
        this.pageNo = pageNo-1;
        this.pageSize = pageSize;
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        Client  client = getEsClient();
        List<Map<String, Object>> restltList = null;
        try {
            restltList =  elasticsearchUtil.queryPageList(client,boolQueryBuilder,pageNo,pageSize,"quotaDate");
        }catch (Exception e){
            e.getMessage();
        }finally {
            client.close();
        }
        return restltList;
    }

    public int getQuotaTotalCount(TjQuota tjQuota,String filters) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        Client  client = getEsClient();
        int count  = 0;
        try {
            count  = (int)elasticsearchUtil.getTotalCount(client,boolQueryBuilder);
        }catch (Exception e){
            e.getMessage();
        }finally {
            client.close();
        }
        return count;

    }

    public List<Map<String, Object>> getQuotaReport(TjQuota tjQuota, String filters,int size) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        Client  client = getEsClient();
        List<Map<String, Object>> list = null;
        try {
           list = elasticsearchUtil.queryList(client,boolQueryBuilder, "quotaDate",size);
        }catch (Exception e){
            e.getMessage();
        }finally {
            client.close();
        }
        return  list;
    }

    public BoolQueryBuilder getBoolQueryBuilder(BoolQueryBuilder boolQueryBuilder){

        if( !StringUtils.isEmpty(result)){
            RangeQueryBuilder rangeQueryResult = QueryBuilders.rangeQuery("result").gte(result);
            boolQueryBuilder.must(rangeQueryResult);
        }
        if( !StringUtils.isEmpty(quotaCode)){
//            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery("quotaCode", quotaCode);
            QueryStringQueryBuilder termQuotaCode = QueryBuilders.queryStringQuery("quotaCode:" + quotaCode);
            boolQueryBuilder.must(termQuotaCode);
        }
        if( !StringUtils.isEmpty(orgName) ){
//            TermQueryBuilder termQueryOrgName = QueryBuilders.termQuery("orgName", orgName);
            QueryStringQueryBuilder termOrgName = QueryBuilders.queryStringQuery("orgName:" + orgName);
            boolQueryBuilder.must(termOrgName);
        }
        if( !StringUtils.isEmpty(org) ){
            String [] orgvals =org.split(",");
            for(int i=0;i<orgvals.length ; i++){
                QueryStringQueryBuilder termOrg = QueryBuilders.queryStringQuery("org:" +  orgvals[i]);
                boolQueryBuilder.mustNot(termOrg);
            }
        }
        if( !StringUtils.isEmpty(slaveKey1) ){
            QueryStringQueryBuilder termSlaveKey1 = QueryBuilders.queryStringQuery("slaveKey1:" + slaveKey1);
            boolQueryBuilder.must(termSlaveKey1);
        }
        if( !StringUtils.isEmpty(slaveKey2) ){
            QueryStringQueryBuilder termSlaveKey2 = QueryBuilders.queryStringQuery("slaveKey2:" + slaveKey2);
            boolQueryBuilder.must(termSlaveKey2);
        }
        if( !StringUtils.isEmpty(province) ){
            QueryStringQueryBuilder termProvince = QueryBuilders.queryStringQuery("province:" + province);
            boolQueryBuilder.must(termProvince);
        }
        if( !StringUtils.isEmpty(city) ){
            QueryStringQueryBuilder termCity = QueryBuilders.queryStringQuery("city:" + city);
            boolQueryBuilder.must(termCity);
        }
        if( !StringUtils.isEmpty(town) ){
            QueryStringQueryBuilder termTown = QueryBuilders.queryStringQuery("town:" + town);
            boolQueryBuilder.must(termTown);
        }
        if( !StringUtils.isEmpty(startTime) ){
            RangeQueryBuilder rangeQueryStartTime = QueryBuilders.rangeQuery("quotaDate").gte(startTime);
            boolQueryBuilder.must(rangeQueryStartTime);
        }
        if( !StringUtils.isEmpty(endTime)){
            RangeQueryBuilder rangeQueryEndTime = QueryBuilders.rangeQuery("quotaDate").lte(endTime);
            boolQueryBuilder.must(rangeQueryEndTime);
        }
        return boolQueryBuilder;
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



    //指标分组统计数量
    public List<Map<String, Object>> searcherByGroup(TjQuota tjQuota, String filters,String aggsField ) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        Client client = getEsClient();
        List<Map<String, Object>> list = null;
        try {
            list = elasticsearchUtil.searcherByGroup(client, boolQueryBuilder, aggsField, "result");
        }catch (Exception e){
            e.getMessage();
        }finally {
            client.close();
        }
        return  list;
    }

    //根据mysql 指标分组求和
    public Map<String, Integer> searcherByGroupBySql(TjQuota tjQuota , String aggsFields ,String filter) throws Exception {
        initialize(tjQuota,null);
        if(StringUtils.isEmpty(filter)){
            filter =  " quotaCode='" + tjQuota.getCode() + "' ";
        }else {
            filter = filter + " ,quotaCode='" + tjQuota.getCode() + "' ";
        }
        Client client = getEsClient();
        Map<String, Integer> map = null;
        try {
            map = elasticsearchUtil.searcherByGroupBySql(client, esConfig.getIndex(), aggsFields, filter, "result");;
        }catch (Exception e){
            e.getMessage();
        }finally {
            client.close();
        }
        return map;
    }



}
