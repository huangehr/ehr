package com.yihu.ehr.analyze.service.pack;

import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.quality.MProfileInfo;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.redis.schema.OrgKeySchema;
import com.yihu.ehr.redis.schema.RsAdapterMetaKeySchema;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.RangeFacet;
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
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SolrUtil solrUtil;
    @Value("${quality.cloud}")
    private String cloud;
    @Autowired
    private OrgKeySchema orgKeySchema;
    @Autowired
    private RsAdapterMetaKeySchema rsAdapterMetaKeySchema;
    protected final Log logger = LogFactory.getLog(this.getClass());
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
        RangeQueryBuilder startRange = QueryBuilders.rangeQuery("event_date");
        startRange.gte(startDate);
        boolQueryBuilder.must(startRange);

        RangeQueryBuilder endRange = QueryBuilders.rangeQuery("event_date");
        endRange.lt(DateUtil.toString(end));
        boolQueryBuilder.must(endRange);

        if (StringUtils.isNotEmpty(orgCode)&&!cloud.equals(orgCode)) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("org_code", orgCode);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        List<Map<String, Object>> res = elasticSearchUtil.list("qc","daily_report", boolQueryBuilder);
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
    public List<Map<String, Object>> getResourceSuccessList(String startDate, String endDate, String orgCode) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pack_type=1;archive_status=3;");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
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
            if(item.getDocCount()>0 && !"".equals(item.getKeyAsString())) {
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
        total.put("date", "总计");
        total.put("inpatient", inpatient_total);
        total.put("oupatient", oupatient_total);
        total.put("physical", physical_total);
        total.put("total", inpatient_total + oupatient_total + physical_total);
        resultList.add(0,total);
        return resultList;
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
        List<Map<String, Object>> resultList = getResourceSuccessList(startDate, endDate, orgCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(resultList);
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
    public Envelop resourceSuccess(String startDate, String endDate, String orgCode,int size,int page) throws Exception {
        List<Map<String, Object>> resultList = getResourceSuccessList(startDate, endDate, orgCode);
        //设置假分页
        return getPageEnvelop(page,size,resultList);
    }

    private Envelop getPageEnvelop(int page,int size,List totalList){
        Envelop envelop = new Envelop();
        //设置假分页
        int totalCount = totalList.size();
        envelop.setTotalCount(totalCount);
        int totalPage = totalCount%size==0 ? totalCount%size:totalCount%size+1;
        envelop.setTotalPage(totalPage);
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        List<Map<String, Object>> pagedList = getPageList(page, size, totalList);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(pagedList);
        return envelop;
    }

    private List getPageList(int pageNum,int pageSize,List data) {
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNum * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
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
            stringBuilder.append("org_code=" + orgCode+";");
        }
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Long total = elasticSearchUtil.count("json_archives", "info", stringBuilder.toString());
        resultMap.put("total", total);
        resultList.add(resultMap);
        envelop.setDetailModelList(resultList);
        envelop.setSuccessFlg(true);
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
    public List<Map<String,Object>> getDataList(String startDate, String endDate, String orgCode) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(count) as count ,SUM(row) as row, dataset_name, dataset ");
        sql.append("FROM json_archives_qc/qc_dataset_detail");
        sql.append(" WHERE receive_date>='" + startDate + " 00:00:00' and receive_date<='" + endDate + " 23:59:59'");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            sql.append(" and org_code='" + orgCode +"'");
        }
        sql.append("GROUP BY dataset_name,dataset");
        List<String> field = new ArrayList<>();
        field.add("count");
        field.add("row");
        field.add("dataset_name");
        field.add("dataset");
        List<Map<String,Object>> list = elasticSearchUtil.findBySql(field, sql.toString());
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("dataset","总计");
        totalMap.put("dataset_name","-");
        double rowTotal = 0;
        double countTotal = 0;
        for(Map<String,Object> map :list){
            map.put("name" ,map.get("dataset_name"));
            rowTotal += Double.valueOf(map.get("row").toString());
            countTotal += Double.valueOf(map.get("count").toString());
        }
        totalMap.put("row",rowTotal);
        totalMap.put("count",countTotal);
        list.add(0,totalMap);
        return list;
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
        List<Map<String, Object>> list = getDataList(startDate, endDate, orgCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(list);
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
    public Envelop getDataSetListPage(String startDate, String endDate, String orgCode,int size,int page) throws Exception {
        List<Map<String, Object>> list = getDataList(startDate, endDate, orgCode);
        return getPageEnvelop(page,size,list);
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

    public Envelop datasetDetail(String date) throws Exception{
        Envelop envelop = new Envelop();
        List<String> field = new ArrayList<>();
        field.add("org_code");
        String sqlOrg = "SELECT org_code FROM json_archives/info where receive_date>= '"+date+" 00:00:00' AND receive_date<='" +  date + " 23:59:59' group by org_code";
        List<Map<String, Object>> orgList = elasticSearchUtil.findBySql(field,sqlOrg);
        for(Map<String,Object> orgMap : orgList) {
            String orgCode = orgMap.get("org_code")+"";
            List<Map<String, Object>> res = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("qc_step=1;");
            stringBuilder.append("receive_date>=" + date + " 00:00:00;");
            stringBuilder.append("receive_date<" + date + " 23:59:59;");
            stringBuilder.append("org_code=" + orgCode);
            long starttime = System.currentTimeMillis();
            int count = (int) elasticSearchUtil.count("json_archives_qc", "qc_dataset_info", stringBuilder.toString());
            double pageNum = count % 1000 > 0 ? count / 1000 + 1 : count / 1000;
            for (int i = 0; i < pageNum; i++) {
                Page<Map<String, Object>> result = elasticSearchUtil.page("json_archives_qc", "qc_dataset_info", stringBuilder.toString(), i + 1, 1000);
                logger.info("查询耗时：" + (System.currentTimeMillis() - starttime) + "ms");
                for (Map<String, Object> map : result) {
                    String eventType = map.get("event_type").toString();
                    List<Map<String, Object>> dataSets = objectMapper.readValue(map.get("details").toString(), List.class);
                    for (Map<String, Object> dataSet : dataSets) {
                        for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
                            getDataSetsDetail(map.get("version") + "", entry.getKey(), (int) entry.getValue(), res, date, orgCode, eventType);
                        }
                    }
                }
            }
            elasticSearchUtil.bulkIndex("json_archives_qc","qc_dataset_detail",res);
            logger.info("统计耗时：" + (System.currentTimeMillis() - starttime) + "ms");
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    public void getDataSetsDetail(String version, String dataSet, int row, List<Map<String, Object>> res,String date,String orgCode,String eventType){
        boolean flag = true;
        for(Map<String, Object> map : res){
            if(dataSet.equals(map.get("dataset"))&&eventType.equals(map.get("event_type"))){
                flag = false;
                map.put("row", (int)map.get("row") + row);
                map.put("count", (int)map.get("count") + 1);
                break;
            }
        }
        if(flag){
            Map<String, Object> map = new HashMap<>();
            map.put("org_code", orgCode);
            map.put("event_type", eventType);
            map.put("receive_date", date+" 00:00:00");
            map.put("dataset", dataSet);
            map.put("dataset_name", redisClient.get("std_data_set_" + version + ":" + dataSet + ":name"));
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
    public List<Map<String, Object>> getArchiveFailedList(String startDate, String endDate, String orgCode) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("archive_status=2;pack_type=1;");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
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
        Map<String, Object> totalMap = new HashMap<>();
        double totalCount = 0.0;
        for(Terms.Bucket item: longTerms.getBuckets()){
            Map<String, Object> temp = new HashMap<>();
            temp.put("error_type", item.getKeyAsString());
            temp.put("error_count", item.getDocCount());
            totalCount += item.getDocCount();
            resultList.add(temp);
        }
        totalMap.put("error_type","total");
        totalMap.put("error_count",totalCount);
        resultList.add(0,totalMap);
        return resultList;
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
        List<Map<String, Object>> resultList = getArchiveFailedList(startDate,endDate,orgCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(resultList);
        return envelop;
    }

    /**
     * 获取资源化解析失败
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop archiveFailed(String startDate, String endDate, String orgCode,int size,int page) throws Exception {
        List<Map<String, Object>> resultList = getArchiveFailedList(startDate,endDate,orgCode);
        Envelop pageEnvelop = getPageEnvelop(page, size, resultList);
        return pageEnvelop;
    }

    /**
     * 获取数据元异常
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getMetadaErrorList(String step, String startDate, String endDate, String orgCode) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("qc_step="+step+";");
        stringBuilder.append("receive_date>=" + startDate + " 00:00:00;");
        stringBuilder.append("receive_date<" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
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
        Map<String, Object> total = new HashMap<>();
        double totalNum = 0.0;
        for(Terms.Bucket item: longTerms.getBuckets()){
            Map<String, Object> temp = new HashMap<>();
            temp.put("error_type", item.getKeyAsString());
            long docCount = item.getDocCount();
            temp.put("error_count",docCount );
            totalNum += docCount;
            resultList.add(temp);
        }
        total.put("error_type","total");
        total.put("error_count",totalNum);
        resultList.add(0,total);
        return resultList;
    }

    /**
     * 获取数据元异常
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop metadataError(String step, String startDate, String endDate, String orgCode) throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList = getMetadaErrorList(step, startDate, endDate, orgCode);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(resultList);
        return envelop;
    }

    /**
     * 获取数据元异常
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop metadataError(String step, String startDate, String endDate, String orgCode,int size,int page) throws Exception {
        List<Map<String, Object>> resultList = getMetadaErrorList(step, startDate, endDate, orgCode);
        return getPageEnvelop(page, size, resultList);
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
    public Page<Map<String, Object>> analyzeErrorList(String filters, String sorts, int page, int size) throws Exception {
        return elasticSearchUtil.page("json_archives","info", filters, sorts, page, size);
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
    public Page<Map<String, Object>> metadataErrorList(String filters, String sorts, int page, int size) throws Exception {
        Page<Map<String, Object>> result = elasticSearchUtil.page("json_archives_qc","qc_metadata_info", filters, sorts, page, size);
        for (Map<String, Object> map : result){
            if(map.get("org_name") == null && map.get("org_code") != null){
                map.put("org_name",orgKeySchema.get(map.get("org_code")+""));
            }
            map.put("dataset_name", redisClient.get("std_data_set_" + map.get("version") + ":" + map.get("dataset") + ":name"));
            map.put("metadata_name", redisClient.get("std_meta_data_" + map.get("version") + ":" + map.get("dataset")+"."+ map.get("metadata")+ ":name"));
        }
        return result;
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
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> metedata = elasticSearchUtil.findById("json_archives_qc","qc_metadata_info",id);
        if(metedata.get("org_name") == null && metedata.get("org_code") != null){
            metedata.put("org_name",orgKeySchema.get(metedata.get("org_code")+""));
        }
        //查找包密码
        Map<String, Object> jsonArchives = elasticSearchUtil.findById("json_archives","info",metedata.get("pack_id")+"");
        if(jsonArchives != null){
            metedata.put("pwd",jsonArchives.get("pwd"));
        }
        if("2".equals(metedata.get("qc_step")+"")){//资源化
            String sql = "SELECT * FROM rs_adapter_scheme WHERE adapter_version='"+metedata.get("version")+"'";
            List<Map<String, Object>> schemeList = jdbcTemplate.queryForList(sql);
            if(schemeList!=null&&schemeList.size()>0){
                metedata.put("scheme", schemeList.get(0).get("NAME"));
            }
            //增加资源化信息
            Map<String,Object> resourceInfo = new HashMap<>();
            String version = metedata.get("version")+"";
            String datasetCode = metedata.get("dataset")+"";
            String metadaCode = metedata.get("metadata")+"";
            resourceInfo.put("originDatasetCode",datasetCode);
            String originDatasetName = redisClient.get("std_data_set_" + metedata.get("version") + ":" + metedata.get("dataset") + ":name");
            resourceInfo.put("originDatasetName",originDatasetName);
            resourceInfo.put("originMetadataCode",metadaCode);
            String originMetadataName = redisClient.get("std_meta_data_" + metedata.get("version") + ":" + metedata.get("dataset")+"."+ metedata.get("metadata")+ ":name");
            resourceInfo.put("originMetadataName",originMetadataName);
            //获取资源化的名称编码
            String targetMetadataCode = rsAdapterMetaKeySchema.getMetaData(version, datasetCode, metadaCode);
            if(StringUtils.isNotBlank(targetMetadataCode)){
                resourceInfo.put("targetMetadataCode",targetMetadataCode);
                String querySql = "SELECT * FROM rs_metadata WHERE id='"+targetMetadataCode+"'";
                List<Map<String, Object>> resourMetadata = jdbcTemplate.queryForList(querySql);
                if(!CollectionUtils.isEmpty(resourMetadata)){
                    resourceInfo.put("targetMetadataName",resourMetadata.get(0).get("name"));
                    Object dict_id = resourMetadata.get(0).get("dict_id");
                    if(dict_id != null && !dict_id.toString().equals("0")){
                        resourceInfo.put("targetDataType","编码");
                    } else {
                        resourceInfo.put("targetDataType","值");
                    }
                }
            }
            String dictId = redisClient.get(String.format("%s:%s:%s", "std_meta_data_"+version, datasetCode+"."+metadaCode, "dict_id"));
            if(StringUtils.isNotBlank(dictId)){
                resourceInfo.put("originDataType","编码");
            } else {
                resourceInfo.put("originDataType","值");
            }
            resourceInfo.put("originValue",metedata.get("value"));
            res.put("resourceInfo",resourceInfo);
        }else if("3".equals(metedata.get("qc_step")+"")){//上传省平台
            //获取适配版本名称
            String adapterName = redisClient.get(metedata.get("adapter_version") + "");
            metedata.put("adapterName", adapterName);
            //增加上传信息
            Map<String,Object> uploadInfo = new HashMap<>();

        }
        metedata.put("dataset_name", redisClient.get("std_data_set_" + metedata.get("version") + ":" + metedata.get("dataset") + ":name"));
        metedata.put("metadata_name", redisClient.get("std_meta_data_" + metedata.get("version") + ":" + metedata.get("dataset")+"."+ metedata.get("metadata")+ ":name"));
        String relationId = metedata.get("org_code")+"_"+metedata.get("event_no")+"_"+ DateUtil.strToDate(metedata.get("event_date")+"").getTime();
        res.put("metedata",metedata);
        Map<String, Object> relation = elasticSearchUtil.findById("archive_relation", "info", relationId);
        if(relation == null){
            relation = new HashMap<>();
        }
        res.put("relation",relation);
        envelop.setObj(res);
        envelop.setSuccessFlg(true);
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
    public Page<Map<String, Object>> archiveList(String filters, String sorts, int page, int size) throws Exception {
        long starttime = System.currentTimeMillis();
        Page<Map<String, Object>> result = elasticSearchUtil.page("json_archives","info", filters, sorts, page, size);
        logger.info("查询耗时：" + (System.currentTimeMillis() - starttime) + "ms");
        return result;
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
        Map<String, Object> archive = elasticSearchUtil.findById("json_archives","info",id);
        res.put("archive",archive);
        Map<String, Object> relation = elasticSearchUtil.findById("archive_relation", "info", archive.get("profile_id") + "");
        if(relation == null){
            relation = new HashMap<>();
        }
        res.put("relation",relation);
        envelop.setObj(res);
        envelop.setSuccessFlg(true);
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
    public Page<Map<String, Object>> uploadRecordList(String filters, String sorts, int page, int size) throws Exception {
        return elasticSearchUtil.page("upload", "record", filters, sorts, page, size);
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
        Map<String, Object> uploadRecord = elasticSearchUtil.findById("upload","record",id);
        List<Map<String, Object>> datasets = new ArrayList<>();
        if(uploadRecord.get("missing")!=null) {
            List<String> missing = (List<String>)uploadRecord.get("missing");
            for (String code : missing) {
                Map<String, Object> dataset = new HashMap<>();
                dataset.put("code", code);
                dataset.put("name", redisClient.get("std_data_set_" + uploadRecord.get("version") + ":" + code + ":name"));
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
                    dataset.put("name", redisClient.get("std_data_set_" + uploadRecord.get("version") + ":" + entry.getKey() + ":name"));
                    dataset.put("status", "已上传");
                    datasets.add(dataset);
                }
            }
        }
        res.put("uploadRecord", uploadRecord);
        res.put("datasets", datasets);
        envelop.setSuccessFlg(true);
        envelop.setObj(res);
        return envelop;
    }

    /**
     * 通过solr查询某一段日期,各类型就诊数据
     * 返回list<Map<String,>>
     * @param startDate
     * @param endDate
     * @param orgCode
     */
    public List<MProfileInfo> getProfileInfo(String startDate, String endDate, String orgCode) throws Exception {
        String q = "event_date:["+startDate+"T00:00:00Z TO "+ endDate +"T23:59:59Z]";
        if(StringUtils.isNotBlank(orgCode)){
            q += " AND org_code="+orgCode;
        }

        //查找门诊
        List<RangeFacet> outRangeFacet = solrUtil.getFacetDateRange("HealthProfile", "event_date", startDate+"T23:59:59Z", endDate+"T23:59:59Z", "+1DAY", "event_type:0",q);
        RangeFacet outPatientRangeFacet = outRangeFacet.get(0);
        List<RangeFacet.Count> outCounts = outPatientRangeFacet.getCounts();
        //查找住院
        List<RangeFacet> inRangeFacet = solrUtil.getFacetDateRange("HealthProfile", "event_date", startDate+"T23:59:59Z", endDate+"T23:59:59Z", "+1DAY", "event_type:1",q);
        RangeFacet inpatientRangeFacet = inRangeFacet.get(0);
        List<RangeFacet.Count> inCounts = inpatientRangeFacet.getCounts();
        //查找体检
        List<RangeFacet> examRangeFacet = solrUtil.getFacetDateRange("HealthProfile", "event_date", startDate+"T23:59:59Z", endDate+"T23:59:59Z", "+1DAY", "event_type:2",q);
        RangeFacet healRangeFacet = examRangeFacet.get(0);
        List<RangeFacet.Count> healCounts = healRangeFacet.getCounts();

        //前端需要list
//        Map<String, Map<String,Integer>> dataMap = new LinkedHashMap<>(outRangeFacet.size());
//        for (int i=0;i<inCounts.size();i++) {
//            Map<String, Integer> map = new LinkedHashMap<>();
//            map.put("out",outCounts.get(i).getCount());
//            map.put("inp",inCounts.get(i).getCount());
//            map.put("heal",healCounts.get(i).getCount());
//            String date = outCounts.get(i).getValue().substring(0, 10);//日期
//            dataMap.put(date, map);
//        }
//        return dataMap;
        int healExamTotal = 0;
        int outTotal = 0;
        int inTotal = 0;
        int hosTotal = 0;
        List<MProfileInfo> list = new ArrayList();
        for (int i=0;i<inCounts.size();i++) {
            MProfileInfo profileInfo = new MProfileInfo();
            profileInfo.setData(outCounts.get(i).getValue().substring(0, 10));
            int healCount = healCounts.get(i).getCount();
            healExamTotal += healCount;
            profileInfo.setHealExam(healCount);
            int inCount = inCounts.get(i).getCount();
            inTotal += inCount;
            profileInfo.setInpatient(inCount);
            int outCount = outCounts.get(i).getCount();
            outTotal += outCount;
            profileInfo.setOutpatient(outCount);
            int total = healCount + inCount + outCount;
            profileInfo.setTotal(total);
            hosTotal += total;
            list.add(profileInfo);
        }
        MProfileInfo profileInfo = new MProfileInfo();
        profileInfo.setData("总计");
        profileInfo.setHealExam(healExamTotal);
        profileInfo.setInpatient(inTotal);
        profileInfo.setOutpatient(outTotal);
        profileInfo.setTotal(hosTotal);
        list.add(0,profileInfo);
        return list;
    }
}
