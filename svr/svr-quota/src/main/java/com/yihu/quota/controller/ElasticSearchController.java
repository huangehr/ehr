package com.yihu.quota.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchConfig;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.vo.PersonalInfoModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by lyr on 2016/7/26.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "elasticSearch", description = "es库数据处理工具")
public class ElasticSearchController extends BaseController {

    @Autowired
    private  ObjectMapper objectMapper;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private EsClientUtil esClientUtil;
    @Autowired
    private ElasticsearchUtil searchUtil;
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private ElasticSearchConfig elasticSearchConfig;
    @Autowired
    private SolrQuery solrQuery;

    @ApiOperation("sql查询es")
    @RequestMapping(value = "/open/getEsInfoBySql", method = RequestMethod.GET)
    public Envelop getEsInfoBySql(
            @ApiParam(value = "sql", name = "查询的SQL")
            @RequestParam(value = "sql") String sql) {
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(sql)) {
            envelop.setErrorMsg("sql为空");
            return envelop;
        }
        try {
            List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
            envelop.setDetailModelList(listData);
        } catch (Exception e) {
            envelop.setErrorMsg("sql执行出错");
        }
        return envelop;
    }

    @ApiOperation("根据index查询es")
    @RequestMapping(value = "/open/getEsInfoByIndexAndParam", method = RequestMethod.GET)
    public Envelop getEsInfoByIndexAndParam(
            @ApiParam(value = "index", name = "es索引")
            @RequestParam(value = "index") String index,
            @ApiParam(value = "whereCondition", name = "where 条件")
            @RequestParam(value = "whereCondition", required = false) String whereCondition) {
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(index)) {
            envelop.setErrorMsg("es索引为空");
            return envelop;
        }
        try {
            StringBuffer sb = new StringBuffer("select * from ");
            sb.append(index);
            if (StringUtils.isNotEmpty(whereCondition)) {
                sb.append(" where ").append(whereCondition).append(" limit 10000");
            }
            List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sb.toString());
            envelop.setDetailModelList(listData);
            envelop.setObj(listData.size());
        } catch (Exception e) {
            envelop.setErrorMsg("sql执行出错");
        }
        return envelop;
    }

    @RequestMapping(value = "/getSolrData", method = RequestMethod.GET)
    @ApiOperation("根据条件获取solr 数据,结果不超过5万条")
    public void getSolrData(
            @ApiParam(value = "core 表名，如 HealthProfile")
            @RequestParam(value = "core", required = true) String core,
            @ApiParam(name = "q", value = "查询条件 多个用  AND 拼接")
            @RequestParam(name = "q",required = true) String q,
            @ApiParam(name = "fl", value = "展示字段 多个用  , 拼接 如 org_area,org_code,EHR_000081")
            @RequestParam(name = "fl",required = true) String fl,HttpServletResponse response
    ){
        long rows = 0;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            //建立新的sheet对象（excel的表单）
            HSSFSheet sheet = workbook.createSheet("solr 数据");
            if(StringUtils.isEmpty(fl)){
                HSSFRow row0 = sheet.createRow(0);
                row0.createCell(0).setCellValue("展示字段 不能为空");
            }else {
                fl += ",rowkey";
            }
            String [] fields = fl.split(",");
            rows = solrQuery.count(core,q);
            if(rows > 50000){
                HSSFRow row0 = sheet.createRow(0);
                row0.createCell(0).setCellValue("数据超过五万条，数据量过大");
            }else {
                list =  solrQuery.queryReturnFieldList(core, q, null, null, 0, rows,fields);
                HSSFRow row0 = sheet.createRow(0);
                for(int j = 0;j < fields.length ;j++){
                    row0.createCell(j).setCellValue(fields[j]);
                }

                for(int i = 0;i < list.size() ;i++){
                    Map<String, Object> map = list.get(i);
                    HSSFRow row = sheet.createRow(i+1);
                    for(int j = 0;j < fields.length ;j++){
                        if(map.get(fields[j]) != null){
                            row.createCell(j).setCellValue(map.get(fields[j]).toString());
                        }
                    }
                }
            }

            OutputStream output=response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=details.xls");
            response.setContentType("application/msexcel");
            workbook.write(output);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        FileOutputStream output = new FileOutputStream("E:\\solr"+ String.valueOf(System.currentTimeMillis()) +".xls");
    }

    @RequestMapping(value = "/saveElasticsearchDocument", method = RequestMethod.POST)
    @ApiOperation("添加elasticsearch文档")
    public String saveDocument(
            @ApiParam(value = "json串")
            @RequestParam(value = "jsonString", required = true) String jsonString,
            @ApiParam(name = "index", value = "索引名称")
            @RequestParam(name = "index",required = true) String index,
            @ApiParam(name = "type", value = "类型(表)名称")
            @RequestParam(name = "type",required = true) String type
    ){
        boolean f = false;
        try {
           String clusterName = elasticSearchConfig.getClusterName();
           String nodeStr = elasticSearchConfig.getClusterNodes();
            if(StringUtils.isNotEmpty(nodeStr)){
                String [] nodes = nodeStr.split(",");
                if(nodes.length > 1){
                    String ip = nodes[0].substring(0,nodes[0].indexOf(":")-1);
                    Client client = esClientUtil.getClient(ip, 9300, clusterName);
                    f = elasticsearchUtil.save(client,index,type,jsonString);
                    client.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  String.valueOf(f);
    }

    @RequestMapping(value = "/impFileDocument", method = RequestMethod.POST)
    @ApiOperation("文件导入elasticsearch文档数据")
    public String fileImpDocument(
            @ApiParam(name = "file", value = "file文件")
            @RequestParam(name = "file",required = true) String file,
            @ApiParam(name = "index", value = "索引名称")
            @RequestParam(name = "index",required = true) String index,
            @ApiParam(name = "type", value = "类型(表)名称")
            @RequestParam(name = "type",required = true) String type
    )throws Exception {
        boolean f = false;
        try {
            Client client = null ;
            String clusterName = elasticSearchConfig.getClusterName();
            String nodeStr = elasticSearchConfig.getClusterNodes();
            if(StringUtils.isNotEmpty(nodeStr)){
                String [] nodes = nodeStr.split(",");
                if(nodes.length > 1){
                    String ip = nodes[0].substring(0,nodes[0].indexOf(":")-1);
                    client = esClientUtil.getClient(ip, 9300, clusterName);
                }
            }
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
                        f = elasticsearchUtil.save(client,index,type,jsonString);
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
                        client.close();
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



    @RequestMapping(value = "/expDocument", method = RequestMethod.POST)
    @ApiOperation("文件导出elasticsearch文档数据")
    public String fileExpDocument(
            @ApiParam(name = "index", value = "索引名称")
            @RequestParam(name = "index",required = true) String index,
            @ApiParam(name = "type", value = "类型(表)名称")
            @RequestParam(name = "type",required = true) String type
    )throws Exception {
        boolean f = false;
        try {
            Client client = null ;
            String clusterName = elasticSearchConfig.getClusterName();
            String nodeStr = elasticSearchConfig.getClusterNodes();
            if(StringUtils.isNotEmpty(nodeStr)){
                String [] nodes = nodeStr.split(",");
                if(nodes.length > 1){
                    String ip = nodes[0].substring(0,nodes[0].indexOf(":")-1);
                    client = esClientUtil.getClient(ip, 9300, clusterName);
                }
            }
            List<Map<String, Object>> list = elasticsearchUtil.queryList(client,index,type, null, null, 10000);
            byte[] buff = new byte[]{};
            StringBuffer docmBuff = new StringBuffer();
            for(Map<String, Object> map:list){
//                String title = "\n{”index”:{”_index”:”quota”,”_type”:”quota_test”}}";
                String document = "\n{";
                for(String key : map.keySet()){
                    if( !key.equals("id")){
                        document = document +"”" + key + "”:”" + map.get(key) +"”,";
                    }
                }
                document = document.substring(0,document.length()-1)+ "}";
//                title = title.replaceAll("”","\"");
                document = document.replaceAll("”","\"");
//                System.out.println(title);
//                System.out.println(document);
                docmBuff = docmBuff.append(document);

            }
            buff = docmBuff.toString().getBytes();
            FileOutputStream out=new FileOutputStream("E://quota.json");
            out.write(buff,0,buff.length);

            out.close();
            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  String.valueOf(f);
    }


    @RequestMapping(value = "/getElasticsearchDocument", method = RequestMethod.POST)
    @ApiOperation("查询elasticsearch文档")
    public List<Map<String, Object>> getElasticsearchDocument(
            @ApiParam(value = "term")
            @RequestParam(value = "term", required = true) String term,
            @ApiParam(value = "value")
            @RequestParam(value = "value", required = true) String value,
            @ApiParam(name = "index", value = "索引名称")
            @RequestParam(name = "index",required = true) String index,
            @ApiParam(name = "type", value = "类型(表)名称")
            @RequestParam(name = "type",required = true) String type
    ){
        List<Map<String, Object>> list = null;
        try {
            Client client = null ;
            String clusterName = elasticSearchConfig.getClusterName();
            String nodeStr = elasticSearchConfig.getClusterNodes();
            if(StringUtils.isNotEmpty(nodeStr)){
                String [] nodes = nodeStr.split(",");
                if(nodes.length > 1){
                    String ip = nodes[0].substring(0,nodes[0].indexOf(":")-1);
                    client = esClientUtil.getClient(ip, 9300, clusterName);
                }
            }
            BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery(term, value);
            boolQueryBuilder.must(termQueryQuotaCode);

            list = elasticsearchUtil.queryList(client,index,type, boolQueryBuilder, null, 200);
            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  list;
    }

    @RequestMapping(value = "/elasticSearch/addElasticSearch", method = RequestMethod.POST)
    @ApiOperation("elasticsearch文档数据")
    public Boolean addElasticSearch(
            @ApiParam(name = "index", value = "索引名称")
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "索引类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "sourceList", value = "值")
            @RequestParam(value = "sourceList") String sourceList) throws Exception {
        boolean f = false;
        try {
            Client client = null ;
            String clusterName = elasticSearchConfig.getClusterName();
            String nodeStr = elasticSearchConfig.getClusterNodes();
            if(StringUtils.isNotEmpty(nodeStr)){
                String [] nodes = nodeStr.split(",");
                if(nodes.length > 1){
                    String ip = nodes[0].substring(0,nodes[0].indexOf(":")-1);
                    client = esClientUtil.getClient(ip, 9300, clusterName);
                }
            }
            InputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null; //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
            try {
                String str = "";
                String jsonString = "";
                sourceList = URLDecoder.decode(sourceList, "UTF-8");
                fis = new ByteArrayInputStream(sourceList.getBytes("UTF-8"));
                // 从文件系统中的某个文件中获取字节
                isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
                br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
                while ((str = br.readLine()) != null) {
                    jsonString = str;
                    System.out.println(jsonString);// 打印
                    //添加到es库
                    f = elasticsearchUtil.save(client,index,type,jsonString);
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
                    client.close();
                    // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  f;
    }


    @RequestMapping(value = "/elasticSearch/testQueryElasticSearch", method = RequestMethod.POST)
    @ApiOperation("测试查询数据")
    public void addElasticSearch(
            @ApiParam(name = "data", value = "参数")
            @RequestParam(value = "data") String data
    ){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String index = "singleDiseasePersonal";
        String type = "personal_info";
        int i =0;
        while(i<2){
            PersonalInfoModel personalInfo = new PersonalInfoModel();
            personalInfo.setDisease("HP0047");
            personalInfo.setDiseaseName("糖尿病");
            personalInfo.setDemographicId(data);
            try {
                String sql = "SELECT count(demographicId) FROM singleDiseasePersonal where demographicId ="+data+" group by demographicId ";
                long count2 = searchUtil.getCountBySql(sql);
                System.out.println("结果条数 count2="+count2);

//                List<Map<String, Object>> relist = elasticSearchUtil.findByField(index, type, "demographicId", data);
//                List<Map<String, Object>> filter = new ArrayList<>();
//                Map<String,Object> paramMap = new HashMap<>();
//                paramMap.put("demographicId",data);
//                filter.add(paramMap);
//               long count = elasticSearchUtil.count(index, type,filter);
//                System.out.println("结果条数 count="+count);
//                System.out.println("结果条数="+ relist.size());
//                if(relist== null || relist.size() ==0){
                if(count2 == 0){
                    Map<String, Object> source = new HashMap<>();
                    String jsonPer = objectMapper.writeValueAsString(personalInfo);
                    source = objectMapper.readValue(jsonPer, Map.class);
                    elasticSearchClient.index(index,type, source);
                }
                i++;
            }catch (Exception e){
                e.getMessage();
            }
        }
    }
}
