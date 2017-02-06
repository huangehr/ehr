package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.ResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2017/1/11.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "hbaseTest", description = "habse测试接口")
public class HbaseTestEndPoint extends EnvelopRestEndPoint {
    @Autowired
    HBaseUtil hbaseUtil;

    @Autowired
    ObjectMapper objectMapper;

    @ApiOperation("模糊匹配表")
    @RequestMapping(value = "getTableList",method = RequestMethod.GET)
    public String getTableList(
            @ApiParam(value="模糊匹配",defaultValue = "")
            @RequestParam String regex) throws Exception
    {
        try {
            String re = "data:[";

            List<String> list = hbaseUtil.getTableList(regex, true);
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

    @ApiOperation("判断表是否存在")
    @RequestMapping(value = "isTableExists",method = RequestMethod.GET)
    public String isTableExists(
            @ApiParam(value="表名",defaultValue = "HH")
            @RequestParam String tableName)
    {
        try {
            if (hbaseUtil.isTableExists(tableName)) {
                return "true";
            } else {
                return "false";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
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
            hbaseUtil.createTable(tableName,cols);
            return "Success create table "+tableName+".";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

    @ApiOperation("删除表")
    @RequestMapping(value = "dropTable",method = RequestMethod.POST)
    public String dropTable(@ApiParam(value="表名",defaultValue = "HH")
                              @RequestParam String tableName)
    {
        try {
            hbaseUtil.dropTable(tableName);
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
    public String insertRecord(@ApiParam(value="表名",defaultValue = "HH")
                                   @RequestParam String tableName,
                               @ApiParam(value="主键",defaultValue = "1")
                               @RequestParam String rowKey,
                               @ApiParam(value="列族",defaultValue = "HH_F1")
                                   @RequestParam String family,
                               @ApiParam(value="列",defaultValue = "HH_C1,HH_C4")
                                   @RequestParam String columns,
                               @ApiParam(value="值",defaultValue = "HH_V1,HH_V4")
                                   @RequestParam String values)
    {
        try {
            Object[] cols = columns.split(",");
            Object[] vals = values.split(",");
            hbaseUtil.insertRecord(tableName,rowKey,family,cols, vals);
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
    public String getOneResult(@ApiParam(value="表名",defaultValue = "HH")
                               @RequestParam String tableName,
                               @ApiParam(value="主键",defaultValue = "1")
                               @RequestParam String rowKey)
    {
        try {

            Map<String, Object> re = hbaseUtil.getResultMap(tableName,rowKey);
            return objectMapper.writeValueAsString(re);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Fail!"+ex.getMessage();
        }
    }

}