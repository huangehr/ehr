package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.hbase.HBaseAdmin;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.solr.SolrAdmin;
import com.yihu.ehr.solr.SolrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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

    @ApiOperation("模糊匹配表")
    @RequestMapping(value = "getTableList", method = RequestMethod.GET)
    public String getTableList(
            @ApiParam(name = "regex", value = "模糊匹配", required = true)
            @RequestParam(value = "regex") String regex) throws Exception {
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

    @ApiOperation("Solr总条数")
    @RequestMapping(value = "countSolr", method = RequestMethod.GET)
    public String countSolr(
            @ApiParam(name = "tableName", value = "表名", defaultValue = "HealthProfile", required = true)
            @RequestParam(value = "tableName") String tableName) throws Exception {
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

    @ApiOperation("Hbase总条数")
    @RequestMapping(value = "countHbase", method = RequestMethod.GET)
    public String countHbase(
            @ApiParam(name = "tableName", value="表名",defaultValue = "HealthProfile")
            @RequestParam(value = "tableName") String tableName) throws Exception {
        Integer count = hbaseDao.count(tableName);
        return "count："+count;

    }

    @ApiOperation("判断表是否存在")
    @RequestMapping(value = "isTableExists", method = RequestMethod.GET)
    public String isTableExists(
            @ApiParam(name = "tableName", value="表名", defaultValue = "HealthProfile")
            @RequestParam(value = "tableName") String tableName) throws Exception {
        if (hbaseAdmin.isTableExists(tableName)) {
            return "true";
        }
        return "false";
    }

    @ApiOperation("创建表")
    @RequestMapping(value = "createTable", method = RequestMethod.POST)
    public String createTable(
            @ApiParam(name = "tableName", value="表名", defaultValue = "HH")
            @RequestParam(value = "tableName") String tableName,
            @ApiParam(name = "columnFamilies", value = "列族，逗号分隔", defaultValue = "HH_F1,HH_F2")
            @RequestParam(value = "columnFamilies") String columnFamilies) throws Exception {
        String[] cols = columnFamilies.split(",");
        hbaseAdmin.createTable(tableName,cols);
        return "Success create table "+tableName+".";
    }

    @ApiOperation("清空表")
    @RequestMapping(value = "truncateTable", method = RequestMethod.POST)
    public String dropTable(
            @ApiParam(name = "tableName", value = "表名", defaultValue = "HealthArchives")
            @RequestParam(value = "tableName") String tableName) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(tableName);
        hbaseAdmin.cleanTable(list);
        //清空索引
        solrAdmin.delete(tableName,"rowkey:*");

        return "Success drop table "+tableName+".";
    }


    @ApiOperation("测试插入数据")
    @RequestMapping(value = "insertRecord", method = RequestMethod.POST)
    public String insertRecord(
            @ApiParam(name = "tableName", value = "表名", defaultValue = "HealthArchives")
            @RequestParam(value = "tableName") String tableName,
            @ApiParam(name = "rowKey", value = "主键", defaultValue = "1")
            @RequestParam(value = "rowKey") String rowKey,
            @ApiParam(name = "family", value = "列族", defaultValue = "basic")
            @RequestParam(value = "family") String family,
            @ApiParam(name = "columns", value = "列", defaultValue = "demographic_id")
            @RequestParam(value = "columns") String columns,
            @ApiParam(name = "values", value = "值", defaultValue = "1234567")
            @RequestParam(value = "values") String values) throws Exception {
        Object[] cols = columns.split(",");
        Object[] vals = values.split(",");
        hbaseDao.add(tableName,rowKey,family,cols, vals);
        return "Success insert record For "+tableName+".";

    }

    @ApiOperation("获取单条数据")
    @RequestMapping(value = "getOneResult", method = RequestMethod.POST)
    public String getOneResult(
            @ApiParam(name = "tableName", value="表名", defaultValue = "HealthProfile")
            @RequestParam(value = "tableName") String tableName,
            @ApiParam(name = "rowKey", value="主键", defaultValue = "1")
            @RequestParam(value = "rowKey") String rowKey) throws Exception {
        Map<String, Object> re = hbaseDao.getResultMap(tableName,rowKey);
        return objectMapper.writeValueAsString(re);
    }


   @ApiOperation("删除单条Hbase")
   @RequestMapping(value = "deleteHbase", method = RequestMethod.POST)
   public String deleteHbase(
           @ApiParam(name = "core", value = "core", defaultValue = "HealthProfile")
           @RequestParam(value = "core") String core,
           @ApiParam(name = "key", value = "key")
           @RequestParam(value = "key") String key) {
       hbaseDao.delete(core,key);
       return "删除单条Hbase成功！";
   }

    @ApiOperation("删除Solr")
    @RequestMapping(value = "deleteSolr", method = RequestMethod.POST)
    @ApiIgnore
    public String deleteSolr(
            @ApiParam(name = "core", value = "core", defaultValue = "HealthProfile")
            @RequestParam(value = "core") String core,
            @ApiParam(name = "key", value = "key")
            @RequestParam(value = "key") String key) throws Exception  {
        solrAdmin.delete(core, key);
        return "删除Solr成功！";
    }

}