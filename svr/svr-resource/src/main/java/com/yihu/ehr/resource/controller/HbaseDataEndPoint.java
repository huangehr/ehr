package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.hbase.HBaseAdmin;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.solr.SolrAdmin;
import com.yihu.ehr.solr.SolrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2017/1/11.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "HbaseDataEndPoint", description = "Hbase数据", tags = {"资源服务-Hbase数据"})
public class HbaseDataEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private HBaseAdmin hbaseAdmin;
    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private SolrAdmin solrAdmin;
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation("模糊匹配表")
    @RequestMapping(value = "getTableList",method = RequestMethod.GET)
    public String getTableList(
            @ApiParam(value="模糊匹配",defaultValue = "")
            @RequestParam String regex) throws Exception
    {
        try {
            String re = "data:[";

            List<String> list = hbaseAdmin.getTableList(regex, true);
            if (list != null && list.size() > 0) {
                for (String item : list) {
                    re += item + "; ";
                }
            }

            re += "]";
            return re;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

    @ApiOperation("Solr总条数")
    @RequestMapping(value = "countSolr",method = RequestMethod.GET)
    public String countSolr(
            @ApiParam(value="表名",defaultValue = "HealthProfile")
            @RequestParam String tableName)
    {
        try {
            long count = solrUtil.count(tableName,"*:*");

            //通过org_code分组统计
            /*Map<String,Long> map = solrUtil.groupCount(tableName,null,null,"org_code",0,1000);
            Long orgCount = new Long(0);
            for(String key : map.keySet())
            {
                orgCount += map.get(key);
            }*/
            return "count:"+count;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

    @ApiOperation("Hbase总条数")
    @RequestMapping(value = "countHbase",method = RequestMethod.GET)
    public String countHbase(
            @ApiParam(value="表名",defaultValue = "HealthProfile")
            @RequestParam String tableName)
    {
        try {
            Integer count = hbaseDao.count(tableName);

            return "count："+count;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

    @ApiOperation("判断表是否存在")
    @RequestMapping(value = "isTableExists",method = RequestMethod.GET)
    public String isTableExists(
            @ApiParam(value="表名",defaultValue = "HealthProfile")
            @RequestParam String tableName) throws Exception {
        if (hbaseAdmin.isTableExists(tableName)) {
            return "true";
        }
        return "false";
    }

    @ApiOperation("创建表")
    @RequestMapping(value = "createTable",method = RequestMethod.POST)
    public String createTable(@ApiParam(value="表名",defaultValue = "HH")
                              @RequestParam String tableName,
                              @ApiParam(value="列族，逗号分隔",defaultValue = "HH_F1,HH_F2")
                              @RequestParam String columnFamilies)
    {
        try {
            String[] cols = columnFamilies.split(",");
            hbaseAdmin.createTable(tableName,cols);
            return "Success create table "+tableName+".";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

    @ApiOperation("清空表")
    @RequestMapping(value = "truncateTable",method = RequestMethod.POST)
    public String dropTable(@ApiParam(value="表名",defaultValue = "HealthArchives")
                              @RequestParam String tableName)
    {
        try {
            List<String> list = new ArrayList<>();
            list.add(tableName);
            hbaseAdmin.cleanTable(list);

            //清空索引
            solrAdmin.delete(tableName,"rowkey:*");

            return "Success drop table "+tableName+".";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }


    @ApiOperation("测试插入数据")
    @RequestMapping(value = "insertRecord",method = RequestMethod.POST)
    public String insertRecord(@ApiParam(value="表名",defaultValue = "HealthArchives")
                                   @RequestParam String tableName,
                               @ApiParam(value="主键",defaultValue = "1")
                               @RequestParam String rowKey,
                               @ApiParam(value="列族",defaultValue = "basic")
                                   @RequestParam String family,
                               @ApiParam(value="列",defaultValue = "demographic_id")
                                   @RequestParam String columns,
                               @ApiParam(value="值",defaultValue = "1234567")
                                   @RequestParam String values)
    {
        try {
            Object[] cols = columns.split(",");
            Object[] vals = values.split(",");
            hbaseDao.add(tableName,rowKey,family,cols, vals);
            return "Success insert record For "+tableName+".";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

    @ApiOperation("获取单条数据")
    @RequestMapping(value = "getOneResult",method = RequestMethod.POST)
    public String getOneResult(@ApiParam(value="表名",defaultValue = "HealthProfile")
                               @RequestParam String tableName,
                               @ApiParam(value="主键",defaultValue = "1")
                               @RequestParam String rowKey)
    {
        try {

            Map<String, Object> re = hbaseDao.getResultMap(tableName,rowKey);
            return objectMapper.writeValueAsString(re);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }


   @ApiOperation("删除单条Hbase")
   @RequestMapping(value = "deleteHbase",method = RequestMethod.POST)
   public String deleteHbase(@ApiParam(value="core",defaultValue = "HealthProfile")
                            @RequestParam String core,
                            @ApiParam(value="key",defaultValue = "")
                            @RequestParam String key)
   {
       try {
           hbaseDao.delete(core,key);
           return "删除单条Hbase成功！";
       }
       catch (Exception ex)
       {
           ex.printStackTrace();
           return ex.getMessage();
       }
   }
    /********************************** solr操作 ********************************************/
    @ApiOperation("删除Solr")
    @RequestMapping(value = "deleteSolr",method = RequestMethod.POST)
    public String deleteSolr(@ApiParam(value="core",defaultValue = "HealthProfile")
                             @RequestParam String core,
                             @ApiParam(value="key",defaultValue = "")
                             @RequestParam String key)
    {
        try {
            solrAdmin.delete(core,key);
            return "删除Solr成功！";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }
}