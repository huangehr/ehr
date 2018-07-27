package com.yihu.quota.etl.extract.es;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.save.TjDataSave;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.service.save.TjDataSaveService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private String townName;
    private String year;
    private String slaveKey1;
    private String slaveKey2;
    private String result;
    private TjQuota tjQuota;
    private String quotaCode;
    private EsConfig esConfig;
    @Autowired
    ElasticsearchUtil elasticsearchUtil;
    @Autowired
    ElasticSearchPool elasticSearchPool;
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
        this.townName = null;
        this.year = null;
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
                        else if(key.equals("townName"))
                            this.townName = params.get(key).toString();
                        else if(key.equals("year"))
                            this.year = params.get(key).toString();
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
        if(null != tjQuota && StringUtils.isNotEmpty(tjQuota.getCode())){
            this.quotaCode = tjQuota.getCode();
        }
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

    public List<Map<String, Object>> queryResultPage(TjQuota tjQuota ,String filters,int pageNo,int pageSize) throws Exception {
        pageNo = (pageNo-1)*pageSize;
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        TransportClient client = elasticSearchPool.getClient();
        List<Map<String, Object>> restltList = null;
        try {
            restltList =  elasticsearchUtil.queryPageList(client, esConfig.getIndex(), esConfig.getType(), boolQueryBuilder, pageNo, pageSize,"quotaDate");
        } catch (Exception e){
            e.getMessage();
        }
        return restltList;
    }

    public int getQuotaTotalCount(TjQuota tjQuota,String filters) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        TransportClient  client = elasticSearchPool.getClient();
        int count  = 0;
        try {
            count  = (int)elasticsearchUtil.getTotalCount(client,esConfig.getIndex(),esConfig.getType(),boolQueryBuilder);
        }catch (Exception e){
            e.getMessage();
        }
        return count;

    }

    public List<Map<String, Object>> getQuotaReport(TjQuota tjQuota, String filters,int size) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        TransportClient  client = elasticSearchPool.getClient();
        List<Map<String, Object>> list = null;
        try {
           list = elasticsearchUtil.queryList(client,esConfig.getIndex(),esConfig.getType(),boolQueryBuilder, "quotaDate",size);
        }catch (Exception e){
            e.getMessage();
        }
        return  list;
    }

    public BoolQueryBuilder getBoolQueryBuilder(BoolQueryBuilder boolQueryBuilder){

        if( !StringUtils.isEmpty(result)){
            if( !result.equals("qb")){//查全部
                result = "1";
                RangeQueryBuilder rangeQueryResult = QueryBuilders.rangeQuery("result").gte(result);
                boolQueryBuilder.must(rangeQueryResult);
            }
        }
        if( !StringUtils.isEmpty(quotaCode)){
//            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery("quotaCode", quotaCode);
            if(esConfig.getType().equals("orgHealthCategoryQuota")){
                QueryStringQueryBuilder termQuotaCode = QueryBuilders.queryStringQuery("orgHealthCategoryQuotaCode:" + quotaCode.replaceAll("_",""));
                boolQueryBuilder.must(termQuotaCode);
            }else{
                QueryStringQueryBuilder termQuotaCode = QueryBuilders.queryStringQuery("quotaCode:" + quotaCode.replaceAll("_",""));
                boolQueryBuilder.must(termQuotaCode);
            }
        }

        BoolQueryBuilder qbChild =  QueryBuilders.boolQuery();

        if( !StringUtils.isEmpty(orgName) ){
//            TermQueryBuilder termQueryOrgName = QueryBuilders.termQuery("orgName", orgName);
            QueryStringQueryBuilder termOrgName = QueryBuilders.queryStringQuery("orgName:" + orgName);
            boolQueryBuilder.must(termOrgName);
        }
        if( !StringUtils.isEmpty(org) ){
            String [] orgvals =org.split(",");
            for(int i=0;i<orgvals.length ; i++){
                MatchQueryBuilder termOrg = QueryBuilders.matchPhraseQuery("org", orgvals[i]);
                qbChild.should(termOrg);
            }
            boolQueryBuilder.must(qbChild);
        }
        if( !StringUtils.isEmpty(slaveKey1) ){
            QueryStringQueryBuilder termSlaveKey1 = QueryBuilders.queryStringQuery("slaveKey1:" + slaveKey1);
            qbChild.should(termSlaveKey1);
            boolQueryBuilder.must(qbChild);
        }
        if( !StringUtils.isEmpty(slaveKey2) ){
            QueryStringQueryBuilder termSlaveKey2 = QueryBuilders.queryStringQuery("slaveKey2:" + slaveKey2);
            qbChild.should(termSlaveKey2);
            boolQueryBuilder.must(qbChild);
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
        if( !StringUtils.isEmpty(townName) ){
            QueryStringQueryBuilder termTown = QueryBuilders.queryStringQuery("townName:" + townName);
            boolQueryBuilder.must(termTown);
        }
        if( !StringUtils.isEmpty(year) ){
            QueryStringQueryBuilder termYear = QueryBuilders.queryStringQuery("year:" + year);
            boolQueryBuilder.must(termYear);
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



    //指标分组统计数量 - 只支持一个字段
    public List<Map<String, Object>> searcherByGroup(TjQuota tjQuota, String filters,String aggsField ) throws Exception {
        initialize(tjQuota,filters);
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        getBoolQueryBuilder(boolQueryBuilder);
        TransportClient client = elasticSearchPool.getClient();
        List<Map<String, Object>> list = null;
        try {
            list = elasticsearchUtil.searcherByGroup(client,esConfig.getIndex(),esConfig.getType(), boolQueryBuilder, aggsField, "result");
        } catch (Exception e){
            e.getMessage();
        }
        return  list;
    }

    //根据mysql 指标分组求和 支持一个和多个字段
    public Map<String, Integer> searcherSumByGroupBySql(TjQuota tjQuota , String aggsFields ,String filter, String sumField,String orderFild,String order) throws Exception {
        initialize(tjQuota,null);
        if(StringUtils.isEmpty(filter)){
            filter =  " quotaCode='" + tjQuota.getCode().replaceAll("_", "") + "' ";
        }else {
            filter = filter + " and quotaCode='" + tjQuota.getCode().replaceAll("_","") + "' ";
        }
        TransportClient client = elasticSearchPool.getClient();
        Map<String, Integer> map = null;
        try {
            map = elasticsearchUtil.searcherSumByGroupBySql(client, esConfig.getIndex(), aggsFields, filter, sumField,orderFild,order);;
        } catch (Exception e){
            e.getMessage();
        }
        return map;
    }




    /**
     * //根据mysql 指标分组 按时间聚合
     * @param tjQuota
     * @param aggsFields
     * @param filter
     * @param dateDime
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> searcherSumByGroupByTime(TjQuota tjQuota , String aggsFields ,String filter,String dateDime) throws Exception {
        initialize(tjQuota,null);
        if(StringUtils.isEmpty(filter)){
            filter =  " quotaCode='" + tjQuota.getCode().replaceAll("_", "") + "' ";
        }else {
            filter = filter + " and quotaCode='" + tjQuota.getCode().replaceAll("_","") + "' ";
        }
        if(StringUtils.isNotEmpty(aggsFields)){
            aggsFields += ",";
        }
        try {
            //SELECT sum(result) FROM medical_service_index group by town,date_histogram(field='quotaDate','interval'='year')
            StringBuffer mysql = new StringBuffer("SELECT ")
                    .append(aggsFields)
                    .append(" sum(result) FROM ").append(esConfig.getIndex())
                    .append(" where quotaDate is not null and ").append(filter)
                    .append(" group by ").append(aggsFields)
                    .append(" date_histogram(field='quotaDate','interval'='")
                    .append(dateDime).append("')").append(" limit 10000 ");
            logger.warn("查询分组 mysql= " + mysql.toString());
            List<Map<String, Object>> listMap = elasticsearchUtil.excuteDataModel(mysql.toString());
            if(listMap != null &&  listMap.size() > 0){
                if(listMap.get(0).get("SUM(result)") != null){
                    return  listMap;
                }
            }
            return  new ArrayList<>();
        } catch (Exception e){
            e.getMessage();
        }
        return null;
    }


    /**
     * 根据sql  分组统计数据
     * @param tjQuota
     * @param aggsFields
     * @param filter
     * @param sumField
     * @param orderFild
     * @param order
     * @return
     * @throws Exception
     */
    public  List<Map<String, Object>>  searcherSumGroup(TjQuota tjQuota , String aggsFields ,String filter, String sumField,String orderFild,String order, String top) throws Exception {
        initialize(tjQuota,null);
        if(StringUtils.isEmpty(filter)){
            filter =  " quotaCode='" + tjQuota.getCode().replaceAll("_", "") + "' ";
        }else {
            filter = filter + " and quotaCode='" + tjQuota.getCode().replaceAll("_","") + "' ";
        }
        try {
            StringBuffer mysql = new StringBuffer("select ");
            mysql.append(aggsFields)
                    .append(" ,sum(").append(sumField).append(") ")
                    .append(" from ").append(esConfig.getIndex())
                    .append(" where quotaDate is not null and ").append(filter)
                    .append(" group by ").append(aggsFields);
            if(StringUtils.isNotEmpty(orderFild) && StringUtils.isNotEmpty(order)){
                if (StringUtils.isNotEmpty(top)) {
                    mysql.append(" order by sum(").append(sumField).append(") desc");
                } else {
                    mysql.append(" order by ").append(orderFild).append(" ").append(order);
                }
            }
            if (StringUtils.isNotEmpty(top)) {
                mysql.append(" limit ").append(top);
            } else {
                mysql.append(" limit 10000 ");
            }
            logger.warn("查询分组 mysql= " + mysql.toString());
            List<Map<String, Object>> listMap = elasticsearchUtil.excuteDataModel(mysql.toString());
            if(listMap != null &&  listMap.size() > 0){
                if(listMap.get(0).get("SUM(result)") != null){
                    return  listMap;
                }
            }
            return  new ArrayList<>();
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }



}
