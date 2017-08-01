package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.extract.ElasticsearchUtil;
import com.yihu.quota.etl.extract.es.EsResultExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.service.quota.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * Created by lyr on 2016/7/26.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "statistics", description = "档案、人次指标统计")
public class SolrTestController extends BaseController {

    @Autowired
    private  ObjectMapper objectMapper;
    @Autowired
    private  StatisticsService statisticsService;
    @Autowired
    private  SolrUtil solrUtil;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private EsResultExtract esResultExtract;


    @RequestMapping(value = "/saveSolrDocument", method = RequestMethod.POST)
    @ApiOperation("添加solr文档")
    public String saveDocument(
            @ApiParam(value = "json串")
            @RequestParam(value = "jsonString", required = true) String jsonString
    ){
        boolean f = false;
        try {
            /***** Solr 保存 ********/
        //{"host": "172.17.110.17","clusterName":"elasticsearch","index":"quota", "type": "quota_test"}
            EsConfig esConfig = new EsConfig();
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            esConfig.setIndex("quota");
            esConfig.setType("quota_test");
            esResultExtract.getEsClient(esConfig);
            f = elasticsearchUtil.save(esResultExtract.getEsClient(esConfig),jsonString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  String.valueOf(f);
    }

    @RequestMapping(value = "/impSolrDocument", method = RequestMethod.POST)
    @ApiOperation("文件导入solr文档")
    public String fileImpDocument(
            @ApiParam(name = "file", value = "file文件")
            @RequestParam(name = "file",required = true) String file,
            HttpServletRequest request )throws Exception {
        boolean f = false;
        try {
            /***** Solr 保存 ********/
            //{"host": "172.17.110.17","clusterName":"elasticsearch","index":"quota", "type": "quota_test"}
            EsConfig esConfig = new EsConfig();
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            esConfig.setIndex("quota");
            esConfig.setType("quota_test");
            esResultExtract.getEsClient(esConfig);

            if( !file.isEmpty()){
                FileInputStream fis = null;
                InputStreamReader isr = null;
                BufferedReader br = null; //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
                try {
                    String str = "";
                    String jsonString = "";
                    fis = new FileInputStream(file);// FileInputStream
                    // 从文件系统中的某个文件中获取字节
                    isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
                    br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
                    while ((str = br.readLine()) != null) {
                        jsonString = str;
                        System.out.println(jsonString);// 打印
                        //添加到es库
                        f = elasticsearchUtil.save(esResultExtract.getEsClient(esConfig),jsonString);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("找不到指定文件");
                } catch (IOException e) {
                    System.out.println("读取文件失败");
                } finally {
                    try {
                        br.close();
                        isr.close();
                        fis.close();
                        // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  String.valueOf(f);
    }



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
