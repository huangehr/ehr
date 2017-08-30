package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.etl.util.EsConfigUtil;
import com.yihu.quota.service.quota.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.response.FacetField;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * Created by lyr on 2016/7/26.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "statistics", description = "solr数据查询")
public class SolrTestController extends BaseController {

    @Autowired
    private  ObjectMapper objectMapper;
    @Autowired
    private  StatisticsService statisticsService;
    @Autowired
    private  SolrUtil solrUtil;


    @RequestMapping(value = "/querySolrDocument", method = RequestMethod.GET)
    @ApiOperation("查询solr文档")
    public List<String> querySolrDocument(
            @ApiParam(value = "查询条件")
            @RequestParam(value = "q", required = false) String q,
            @ApiParam(value = "过滤条件")
            @RequestParam(value = "fq",required = true) String fq
    ){
        List<String> list = new ArrayList<String>();
        try {
            /***** Solr查询 ********/
            String [] fqs = fq.split(";");
//            SolrDocumentList solrList = solrUtil.queryByfqs("HealthProfile", q, fqs, null, 0, 15);

            /***** Hbase查询 ********/
//            if(solrList!=null && solrList.getNumFound()>0){
//                long num = solrList.getNumFound();
//                for (SolrDocument doc : solrList){
//                    String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
//                    list.add(rowkey);
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }



    public Map<String,Integer>  statiscSlor() throws Exception {
        String[] groups = {"event_type","org_code"};
        String fq = "";
        String q = "";
        String core = "HealthProfile";

        String orgCode = null;
        fq = "org_code:" + (StringUtils.isEmpty(orgCode) ? "*" : orgCode);

//        String dq = " &&  create_date:[";
//        //起始时间
//        if (!StringUtils.isEmpty(startDate)) {
//            dq += startDate + "T00:00:00Z";
//        } else {
//            dq += "*";
//        }
//        dq += " TO ";
//        //结束时间
//        if (!StringUtils.isEmpty(endDate)) {
//            dq += endDate + "T23:59:59Z";
//        } else {
//            dq += "*";
//        }
//        dq += "]";
//
//        System.out.println(dq);

        //q 查询条件  fq 过滤条件  group 分组
        List<FacetField>  facetFields = solrUtil.groupCount( core,  q,  fq, groups);
        Map<String,Integer> map = new HashMap<>();
        for (FacetField facet : facetFields) {
            String groupName = facet.getName();
            SolrGroupEntity group = new SolrGroupEntity(groupName);

            List<FacetField.Count> counts = facet.getValues();
            for (FacetField.Count count : counts) {
                String name = count.getName();
                if(!StringUtils.isEmpty(name)){
                    map.put(groupName+ "-" +name, (int)count.getCount());
                }
            }
        }
        return  map;
    }


//    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfile, method = RequestMethod.GET)
//    @ApiOperation("档案入库统计")
//    public Map<String, Integer> profileStatistics(
//            @RequestParam(value = "items")
//            @ApiParam(value = "items") String items,
//            @RequestParam(value = "params")
//            @ApiParam(value = "params") String params) throws Exception {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(params);
//            return statiscSlor();
////            return statisticsService.profileStatistics(items, jsonNode);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ex;
//        }
//    }

//    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsOutpatientHospital, method = RequestMethod.GET)
//    @ApiOperation("门诊住院统计")
//    public Map<String, Object> outpatientAndHospitalStatistics(
//            @RequestParam(value = "items")
//            @ApiParam(value = "items") String items,
//            @RequestParam(value = "params")
//            @ApiParam(value = "params") String params) throws Exception {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(params);
//            return statisticsService.outpatientAndHospitalStatistics(items, jsonNode, false);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ex;
//        }
//    }


}
