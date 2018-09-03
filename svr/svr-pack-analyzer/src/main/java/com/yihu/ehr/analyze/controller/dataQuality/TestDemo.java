package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  临时测试聚合demo，后续删除
 * @author HZY
 * @created 2018/9/3 9:08
 */
@Service
public class TestDemo {

    private final static Logger logger = LoggerFactory.getLogger(TestDemo.class);

    @Autowired
    private ElasticSearchPool elasticSearchPool;

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public List<Map<String, Object>> testAgg(String eventDateStart, String eventDateEnd , String org_area){
        StringBuilder stringBuilder2 = new StringBuilder();
//        stringBuilder2.append("archive_status=3;");
        stringBuilder2.append("pack_type=1;");
        stringBuilder2.append("receive_date>=" + eventDateStart + " 00:00:00;");
        stringBuilder2.append("receive_date<" + eventDateEnd + " 23:59:59;");
        if (org_area!=null) {
            stringBuilder2.append("org_area=" + org_area);
        }

        TransportClient transportClient = elasticSearchPool.getClient();
        List<Map<String, Object>> resultList = new ArrayList<>();
        SearchRequestBuilder builder = transportClient.prepareSearch("json_archives");
        builder.setTypes("info");
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder2.toString()));
        AggregationBuilder terms = AggregationBuilders.terms("org_area").field("org_area").size(200);
        CardinalityBuilder childTerms = AggregationBuilders.cardinality("count").field("event_no").precisionThreshold(40000);
        terms.subAggregation(childTerms);
        builder.addAggregation(terms);
        builder.setSize(0);
        builder.setExplain(true);
        SearchResponse response = builder.get();
        StringTerms longTerms = response.getAggregations().get("org_area");
        for (Terms.Bucket item : longTerms.getBuckets()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("org_area", item.getKeyAsString());
            temp.put("count11", item.getDocCount());
            resultList.add(temp);
        }

        return resultList;
    }


    public List<Map<String, Object>> testAgg2(String start,String end ) throws Exception {
        String dateField = "receive_date";
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<String> orgList = new ArrayList<>();
        Map<String, Object> map = null;
        //统计有数据的医院code
        String sqlOrg = "SELECT org_area FROM json_archives/info where receive_date>= '" + start + " 00:00:00' AND receive_date<='" + end + " 23:59:59' group by org_area ";
        try {
            ResultSet resultSetOrg = elasticSearchUtil.findBySql(sqlOrg);
            while (resultSetOrg.next()) {
                String orgCode = resultSetOrg.getString("org_area");
                orgList.add(orgCode);
            }
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }

        //按医院code查找，直接group by查找结果有问题
        for (String orgCode : orgList) {
            map = new HashMap<>();
            //完整数
            try {
                long starttime = System.currentTimeMillis();
                String sql3 = "";
                if (StringUtils.isNotEmpty(orgCode)) {
                    sql3 = "SELECT COUNT(DISTINCT org_area) FROM json_archives WHERE pack_type=1 AND org_area='" + orgCode + "' AND " + dateField + " BETWEEN " +
                            "'" + start + " 00:00:00' AND '" + end + " 23:59:59'";
                } else {
                    sql3 = "SELECT COUNT(DISTINCT org_area) FROM json_archives WHERE pack_type=1 AND " + dateField +
                            " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59'";
                }

                ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
                resultSet3.next();
                map.put("count", new Double(resultSet3.getObject("COUNT(DISTINCT event_no)").toString()).intValue());//就诊
                map.put("org_area",orgCode);//就诊
                resultList.add(map);
                logger.info("平台就诊人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");
            } catch (Exception e) {
                if (!"Error".equals(e.getMessage())) {
                    e.printStackTrace();
                }
            }

        }
        return resultList;
    }


    public List<Map<String, Object>>  testSearch(){
        TransportClient transportClient = elasticSearchPool.getClient();
        Map<String, Long> resMap = new HashMap<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<String> fileds = new ArrayList<>();
        String filters = "";
        fileds.add("count");
        fileds.add("upload_status");
        //完整数
        long total = 0;
        try {
            long starttime = System.currentTimeMillis();
            SearchRequestBuilder builder = transportClient.prepareSearch("test1");
            builder.setTypes("hzy");
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
//                builder.setQuery(elasticSearchUtil.getQueryBuilder(filters.toString()));
            AggregationBuilder terms = AggregationBuilders.terms("upload_status").field("upload_status").size(200);
            CardinalityBuilder childTerms = AggregationBuilders.cardinality("COUNT(DISTINCT org_area)").field("distinct org_area").precisionThreshold(40000);
            terms.subAggregation(childTerms);
            builder.addAggregation(terms);
            builder.setSize(0);
            builder.setFrom(0);
            Map<String,Object> sourceMap = new HashMap<>();
            List<String> include = new ArrayList<>();
            include.add("COUNT");
            sourceMap.put("includes",include);
            sourceMap.put("excludes",new ArrayList<>());
            builder.setSource(sourceMap);
            SearchResponse response = builder.get();
            StringTerms longTerms = response.getAggregations().get("upload_status");
            for (Terms.Bucket item : longTerms.getBuckets()) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("upload_status", item.getKeyAsString());
                temp.put("count11", item.getDocCount());
                resultList.add(temp);
            }
            logger.info("平台就诊人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }



        return resultList;
    }

    public List<Map<String, Object>> testSearch2(){
        StringBuilder stringBuilder2 = new StringBuilder();
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> resultList2 = new ArrayList<>();
        TransportClient transportClient = elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch("test1");
        builder.setTypes("hzy");
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder2.toString()));
        AggregationBuilder terms = AggregationBuilders.terms("upload_status").field("upload_status").size(200);
        TermsBuilder childTerms = AggregationBuilders.terms("count").field("org_area") ;
        terms.subAggregation(childTerms);
        builder.addAggregation(terms);
        builder.setSize(0);
        builder.setExplain(true);
        SearchResponse response = builder.get();
        StringTerms longTerms = response.getAggregations().get("upload_status");
        for (Terms.Bucket item : longTerms.getBuckets()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("org_area", item.getKeyAsString());
            temp.put("count11", item.getDocCount());
            resultList.add(temp);
        }

        return resultList;
    }

    public void testadd(String type) throws ParseException {
        List<Map<String, Object>> list = null;
        for (int x = 0; x < 30; x++) {
            for (int i = 0; i < 10; i++) {
                list = new ArrayList<>();
                for (int j = 0; j < 10000; j++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("upload_status", i);
                    map.put("event_type", "i：" + i + "下面的：" + j);
                    map.put("org_area", type + j);
                    list.add(map);
                }
                for (int j = 0; j < 10; j++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("upload_status", i);
                    map.put("event_type", "i：" + i + "重复出来的：" + j);
                    map.put("org_area", type + j);
                    list.add(map);
                }
                elasticSearchUtil.bulkIndex("test1", "hzy", list);
            }
        }
    }

    public void testDel() throws ParseException {
        elasticSearchUtil.deleteByFilter("test1","hzy","");
    }

}
