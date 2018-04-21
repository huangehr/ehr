package com.yihu.quota.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.vo.PersonalInfoModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URLDecoder;
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

    private static String HOST = "172.17.110.17";
    private static int PORT = 9300;
    private static String INDEX = "quota_index";
    private static String CLUSTERNAME = "elasticsearch";
    private static String TYPE = "quota";


    public EsConfig config(){
        EsConfig esConfig = new EsConfig();
        esConfig.setHost(HOST);
        esConfig.setPort(PORT);
        esConfig.setClusterName(CLUSTERNAME);
        esConfig.setIndex(INDEX);
        esConfig.setType(TYPE);
        return esConfig;
    }

    @RequestMapping(value = "/saveElasticsearchDocument", method = RequestMethod.POST)
    @ApiOperation("添加elasticsearch文档")
    public String saveDocument(
            @ApiParam(value = "json串")
            @RequestParam(value = "jsonString", required = true) String jsonString
    ){
        boolean f = false;
        try {
            /***** elasticsearch 保存 ********/
            EsConfig esConfig = config();
            esConfig.setIndex(INDEX);
            esConfig.setType(TYPE);
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(), esConfig.getClusterName());
            f = elasticsearchUtil.save(client,esConfig.getIndex(),esConfig.getType(),jsonString);
            client.close();
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
            /***** elasticsearch 保存 ********/
            EsConfig esConfig = config();
            esConfig.setIndex(index);
            esConfig.setType(type);
            esConfig.setHost("172.19.103.9");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(), esConfig.getClusterName());
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
                        f = elasticsearchUtil.save(client,esConfig.getIndex(),esConfig.getType(),jsonString);
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
            /***** elasticsearch 保存 ********/
            EsConfig esConfig = config();
            esConfig.setIndex(index);
            esConfig.setType(type);
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(), esConfig.getClusterName());
            List<Map<String, Object>> list = elasticsearchUtil.queryList(client,esConfig.getIndex(),esConfig.getType(), null, null, 10000);
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
            @RequestParam(value = "value", required = true) String value
    ){
        List<Map<String, Object>> list = null;
        try {
            EsConfig esConfig = config();
            esConfig.setIndex(INDEX);
            esConfig.setType(TYPE);
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(), esConfig.getClusterName());
            BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery(term, value);
            boolQueryBuilder.must(termQueryQuotaCode);

            list = elasticsearchUtil.queryList(client,esConfig.getIndex(),esConfig.getType(), boolQueryBuilder, null, 200);
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
            /***** elasticsearch 保存 ********/
            EsConfig esConfig = config();
            esConfig.setIndex(index);
            esConfig.setType(type);
            esConfig.setHost("172.19.103.9");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(), esConfig.getClusterName());
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

    @Autowired
    private ElasticsearchUtil searchUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchClient elasticSearchClient;

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
