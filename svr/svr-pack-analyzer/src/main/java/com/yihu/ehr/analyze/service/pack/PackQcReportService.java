package com.yihu.ehr.analyze.service.pack;

import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: zhengwei
 * @Date: 2018/5/31 16:21
 * @Description:质控报表
 */
@Service
public class PackQcReportService extends BaseJpaService {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisClient redisClient;
    @Value("${quality.cloud}")
    private String cloud;
    /**
     * 获取医院数据
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop dailyReport(String startDate, String endDate, String orgCode) throws Exception{
        Envelop envelop = new Envelop();
        Date end = DateUtil.addDate(1, DateUtil.formatCharDateYMD(endDate));
        Map<String,Object> resMap = new HashMap<String,Object>();
        List<Map<String,Object>> list = new ArrayList<>();
        int total=0;
        int inpatient=0;
        int oupatient=0;
        int physical=0;
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder startRange = QueryBuilders.rangeQuery("create_date");
        startRange.gte(startDate);
        boolQueryBuilder.must(startRange);

        RangeQueryBuilder endRange = QueryBuilders.rangeQuery("create_date");
        endRange.lt(DateUtil.toString(end));
        boolQueryBuilder.must(endRange);

        if (StringUtils.isNotEmpty(orgCode)&&!cloud.equals(orgCode)) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("org_code", orgCode);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        List<Map<String, Object>> res = elasticSearchClient.findByField("qc","daily_report", boolQueryBuilder);
        if(res!=null && res.size()>0){
            for(Map<String,Object> report : res){
                total+=Integer.parseInt(report.get("HSI07_01_001").toString());
                inpatient+=Integer.parseInt(report.get("HSI07_01_012").toString());
                oupatient+=Integer.parseInt(report.get("HSI07_01_002").toString());
                physical+=Integer.parseInt(report.get("HSI07_01_004").toString());
            }
        }
        resMap.put("total",total);
        resMap.put("inpatient",inpatient);
        resMap.put("oupatient",oupatient);
        resMap.put("physical",physical);
        list.add(resMap);
        envelop.setDetailModelList(list);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取资源化数据
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop resourceSuccess(String startDate, String endDate, String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        String index = "json_archives";
        String type = "info";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pack_type=1;");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode+";");
        }
        // 门诊
        String oupatientStr = stringBuilder.toString() + "event_type=0";
        Long oupatient = elasticSearchUtil.count(index, type, oupatientStr);
        // 住院
        String inpatientStr = stringBuilder.toString() + "event_type=1";
        Long inpatient = elasticSearchUtil.count(index, type, inpatientStr);
        // 体检
        String physicalStr = stringBuilder.toString() + "event_type=2";
        Long physical = elasticSearchUtil.count(index, type, physicalStr);
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultMap.put("oupatient", oupatient);
        resultMap.put("inpatient", inpatient);
        resultMap.put("physical", physical);
        resultMap.put("total", oupatient+inpatient+physical);
        resultList.add(resultMap);
        envelop.setDetailModelList(resultList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取档案数据
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop archiveReport(String startDate, String endDate, String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pack_type=1;");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            SearchRequestBuilder builder = transportClient.prepareSearch("json_archives");
            builder.setTypes("info");
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder.toString()));
            DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder("date");
            dateHistogramBuilder.field("event_date");
            dateHistogramBuilder.interval(DateHistogramInterval.DAY);
            dateHistogramBuilder.format("yyyy-MM-dd");
            dateHistogramBuilder.minDocCount(0);
            AggregationBuilder terms = AggregationBuilders.terms("event_type").field("event_type");
            dateHistogramBuilder.subAggregation(terms);
            builder.addAggregation(dateHistogramBuilder);
            builder.setSize(0);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            Histogram histogram = response.getAggregations().get("date");
            double inpatient_total = 0.0;
            double oupatient_total = 0.0;
            double physical_total = 0.0;
            for(Histogram.Bucket item: histogram.getBuckets()){
                Map<String, Object> temp = new HashMap<>();
                if(item.getDocCount()>0&&!"".equals(item.getKeyAsString())) {
                    temp.put("date", item.getKeyAsString());
                    LongTerms longTerms = item.getAggregations().get("event_type");
                    double inpatient = 0.0;
                    double oupatient = 0.0;
                    double physical = 0.0;
                    for(Terms.Bucket item1 : longTerms.getBuckets()){
                        if("0".equals(item1.getKeyAsString())) {
                            oupatient=item1.getDocCount();
                            oupatient_total+=item1.getDocCount();
                        }else if("1".equals(item1.getKeyAsString())) {
                            inpatient=item1.getDocCount();
                            inpatient_total+=item1.getDocCount();
                        }else if("2".equals(item1.getKeyAsString())) {
                            physical=item1.getDocCount();
                            physical_total+=item1.getDocCount();
                        }
                    }
                    temp.put("inpatient", inpatient);
                    temp.put("oupatient", oupatient);
                    temp.put("physical", physical);
                    temp.put("total", inpatient+oupatient+physical);
                    resultList.add(temp);
                }
            }
            Map<String, Object> total = new HashMap<>();
            total.put("date", "合计");
            total.put("inpatient", inpatient_total);
            total.put("oupatient", oupatient_total);
            total.put("physical", physical_total);
            total.put("total", inpatient_total + oupatient_total + physical_total);
            resultList.add(0,total);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
        return envelop;
    }

    /**
     * 获取数据集列表数据
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop dataSetList(String startDate, String endDate, String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> res = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("qc_step=1;");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        List<Map<String, Object>> list = elasticSearchUtil.list("json_archives_qc", "qc_dataset_info", stringBuilder.toString());
        for(Map<String, Object> map : list){
            List<Map<String,Object>> dataSets = objectMapper.readValue(map.get("details").toString(), List.class);
            for(Map<String, Object> dataSet : dataSets){
                for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
                    getDataSets(map.get("version")+"", entry.getKey(), (int)entry.getValue(), res);
                }
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(res);
        return envelop;
    }

    public void getDataSets(String version, String dataSet, int row, List<Map<String, Object>> res){
        boolean flag = true;
        for(Map<String, Object> map : res){
            if(dataSet.equals(map.get("dataset"))){
                flag = false;
                map.put("row", (int)map.get("row") + row);
                map.put("count", (int)map.get("count") + 1);
                break;
            }
        }
        if(flag){
            Map<String, Object> map = new HashMap<>();
            map.put("dataset", dataSet);
            map.put("name", redisClient.get("std_data_set_" + version + ":" + dataSet + ":name"));
            map.put("row", row);
            map.put("count", 1);
            res.add(map);
        }
    }
    /**
     * 获取资源化解析失败
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop archiveFailed(String startDate, String endDate, String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("archive_status=2;");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            SearchRequestBuilder builder = transportClient.prepareSearch("json_archives");
            builder.setTypes("info");
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder.toString()));
            AggregationBuilder terms = AggregationBuilders.terms("error_type").field("error_type");
            builder.addAggregation(terms);
            builder.setSize(0);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            LongTerms longTerms = response.getAggregations().get("error_type");
            for(Terms.Bucket item: longTerms.getBuckets()){
                Map<String, Object> temp = new HashMap<>();
                temp.put("error_type", item.getKeyAsString());
                temp.put("error_count", item.getDocCount());
                resultList.add(temp);
            }
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
        return envelop;
    }

    /**
     * 获取解析异常
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop metadataError(String step, String startDate, String endDate, String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("qc_step="+step+";");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            SearchRequestBuilder builder = transportClient.prepareSearch("json_archives_qc");
            builder.setTypes("qc_metadata_info");
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder.toString()));
            AggregationBuilder terms = AggregationBuilders.terms("qc_error_type").field("qc_error_type");
            builder.addAggregation(terms);
            builder.setSize(0);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            LongTerms longTerms = response.getAggregations().get("qc_error_type");
            for(Terms.Bucket item: longTerms.getBuckets()){
                Map<String, Object> temp = new HashMap<>();
                temp.put("error_type", item.getKeyAsString());
                temp.put("error_count", item.getDocCount());
                resultList.add(temp);
            }
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
        return envelop;
    }

    /**
     * 解析失败问题查询
     * @param filters
     * @param sorts
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> analyzeErrorList(String filters, String sorts, int page, int size) throws Exception {
        List<Map<String, Object>> orgs = getOrgs();
        List<Map<String, Object>> list = elasticSearchUtil.page("json_archives","info", filters, sorts, page, size);
        for(Map<String, Object> map:list){
            map.put("org_name",getOrgName(orgs, map.get("org_code")+""));
        }
        return list;
    }

    /**
     * 异常数据元查询
     * @param filters
     * @param sorts
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> metadataErrorList(String filters, String sorts, int page, int size) throws Exception {
        List<Map<String, Object>> orgs = getOrgs();
        List<Map<String, Object>> list = elasticSearchUtil.page("json_archives_qc","qc_metadata_info", filters, sorts, page, size);
        for(Map<String, Object> map:list){
            map.put("org_name",getOrgName(orgs, map.get("org_code")+""));
            map.put("dataset_name", redisClient.get("std_data_set_" + map.get("version") + ":" + map.get("dataset") + ":name"));
            map.put("metadata_name", redisClient.get("std_meta_data_" + map.get("version") + ":" + map.get("dataset")+"."+ map.get("metadata")+ ":name"));
        }
        return list;
    }

    public List<Map<String, Object>> getOrgs(){
        return jdbcTemplate.queryForList("SELECT org_code,full_name from organizations");
    }

    public String getOrgName(List<Map<String, Object>> orgs, String orgCode){
        String orgName = "";
        for(Map<String, Object> map : orgs){
            if(orgCode.equals(map.get("ORG_CODE"))){
                orgName = ObjectUtils.toString(map.get("FULL_NAME"));
                break;
            }
        }
        return orgName;
    }

    /**
     * 获取异常详情
     * @param id
     * @return
     * @throws Exception
     */
    public Envelop metadataErrorDetail(String id) throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> orgs = getOrgs();
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> metedata = elasticSearchUtil.findById("json_archives_qc","qc_metadata_info",id);
        if("2".equals(metedata.get("qc_step")+"")){
            String sql = "SELECT * FROM rs_adapter_scheme WHERE adapter_version='"+metedata.get("version")+"'";
            List<Map<String, Object>> schemeList = jdbcTemplate.queryForList(sql);
            if(schemeList!=null&&schemeList.size()>0){
                metedata.put("scheme", schemeList.get(0).get("NAME"));
            }
        }
        metedata.put("org_name",getOrgName(orgs, metedata.get("org_code")+""));
        metedata.put("dataset_name", redisClient.get("std_data_set_" + metedata.get("version") + ":" + metedata.get("dataset") + ":name"));
        metedata.put("metadata_name", redisClient.get("std_meta_data_" + metedata.get("version") + ":" + metedata.get("dataset")+"."+ metedata.get("metadata")+ ":name"));
        String relationId = metedata.get("org_code")+"_"+metedata.get("event_no")+"_"+ DateUtil.strToDate(metedata.get("event_date")+"").getTime();
        res.put("metedata",metedata);
        res.put("relation",elasticSearchUtil.findById("archive_relation","info",relationId));
        envelop.setObj(res);
        return envelop;
    }

    /**
     * 档案包列表
     * @param filters
     * @param sorts
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> archiveList(String filters, String sorts, int page, int size) throws Exception {
        List<Map<String, Object>> orgs = getOrgs();
        List<Map<String, Object>> list = elasticSearchUtil.page("json_archives","info", filters, sorts, page, size);
        for(Map<String, Object> map:list){
            map.put("org_name",getOrgName(orgs, map.get("org_code")+""));
        }
        return list;
    }

    /**
     * 档案包详情
     * @param id
     * @return
     * @throws Exception
     */
    public Envelop archiveDetail(String id) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, Object> res = new HashMap<>();
        List<Map<String, Object>> orgs = getOrgs();
        Map<String, Object> archive = elasticSearchUtil.findById("json_archives","info",id);
        archive.put("org_name",getOrgName(orgs, archive.get("org_code")+""));
        res.put("archive",archive);
        res.put("relation",elasticSearchUtil.findById("archive_relation","info",archive.get("profile_id")+""));
        envelop.setObj(res);
        return envelop;
    }

    /**
     * 上传列表
     * @param filters
     * @param sorts
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> uploadRecordList(String filters, String sorts, int page, int size) throws Exception {
        List<Map<String, Object>> orgs = getOrgs();
        List<Map<String, Object>> list = elasticSearchUtil.page("upload","record", filters, sorts, page, size);
        for(Map<String, Object> map:list){
            map.put("org_name",getOrgName(orgs, map.get("org_code")+""));
        }
        return list;
    }

    /**
     * 上传详情
     * @param id
     * @return
     * @throws Exception
     */
    public Envelop uploadRecordDetail(String id) throws Exception {
        Envelop envelop = new Envelop();
        Map<String,Object> res = new HashMap<>();
        List<Map<String, Object>> orgs = getOrgs();
        Map<String, Object> uploadRecord = elasticSearchUtil.findById("upload","record",id);
        uploadRecord.put("org_name",getOrgName(orgs, uploadRecord.get("org_code")+""));
        List<Map<String, Object>> datasets = new ArrayList<>();
        if(uploadRecord.get("missing")!=null) {
            List<String> missing = objectMapper.readValue(uploadRecord.get("missing").toString(), List.class);
            for (String dataSet : missing) {
                Map<String, Object> dataset = new HashMap<>();
                dataset.put("code", dataSet);
                dataset.put("name", redisClient.get("std_data_set_" + uploadRecord.get("from_version") + ":" + dataSet + ":name"));
                dataset.put("status", "未上传");
                datasets.add(dataset);
            }
        }
        if(uploadRecord.get("datasets")!=null) {
            List<Map<String,Object>> details = objectMapper.readValue(uploadRecord.get("datasets").toString(), List.class);
            for (Map<String, Object> dataSet : details) {
                for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
                    Map<String, Object> dataset = new HashMap<>();
                    dataset.put("code", entry.getKey());
                    dataset.put("name", redisClient.get("std_data_set_" + uploadRecord.get("from_version") + ":" + entry.getKey() + ":name"));
                    dataset.put("status", "已上传");
                    datasets.add(dataset);
                }
            }
        }
        res.put("uploadRecord", uploadRecord);
        res.put("datasets", datasets);
        envelop.setObj(res);
        return envelop;
    }
}
