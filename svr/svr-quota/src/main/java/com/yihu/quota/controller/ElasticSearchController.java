package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.solr.SolrUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EsConfigUtil esConfigUtil;

    private static String host = "172.17.110.17";
    private static int port = 9300;
    private static String index = "quota";
    private static String clusterName = "elasticsearch";
    private static String type = "quota_test";


    public EsConfig config(){
        EsConfig esConfig = new EsConfig();
        esConfig.setHost(host);
        esConfig.setPort(port);
        esConfig.setClusterName(clusterName);
        esConfig.setIndex(index);
        esConfig.setType(type);
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
            esConfig.setIndex(index);
            esConfig.setType(type);
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(),esConfig.getIndex(),esConfig.getType(), esConfig.getClusterName());
            f = elasticsearchUtil.save(client,jsonString);
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
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(),esConfig.getIndex(),esConfig.getType(), esConfig.getClusterName());
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
                        f = elasticsearchUtil.save(client,jsonString);
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
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(),esConfig.getIndex(),esConfig.getType(), esConfig.getClusterName());
            List<Map<String, Object>> list = elasticsearchUtil.queryList(client, null, null, 10000);
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
            @ApiParam(value = "filter")
            @RequestParam(value = "filter", required = true) String filter
    ){
        List<Map<String, Object>> list = null;
        try {
            EsConfig esConfig = config();
            esConfig.setIndex(index);
            esConfig.setType(type);
            esConfig.setHost("172.17.110.17");
            esConfig.setPort(9300);
            esConfig.setClusterName("elasticsearch");
            Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(),esConfig.getIndex(),esConfig.getType(), esConfig.getClusterName());
            BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
            TermQueryBuilder termQueryQuotaCode = QueryBuilders.termQuery("orgName", filter);
            boolQueryBuilder.must(termQueryQuotaCode);

            list = elasticsearchUtil.queryList(client, boolQueryBuilder, null, 200);
            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return  list;
    }

}
