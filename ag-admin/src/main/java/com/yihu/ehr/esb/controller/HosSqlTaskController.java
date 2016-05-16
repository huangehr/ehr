package com.yihu.ehr.esb.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosSqlTaskClient;
import com.yihu.ehr.model.esb.MHosSqlTask;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/esb")
@RestController
@Api(value = "his穿透管理接口", description = "his穿透管理接口" ,tags = {"his穿透管理接口"})
public class HosSqlTaskController extends BaseController {

    @Autowired
    private HosSqlTaskClient hosSqlTaskClient;


    @RequestMapping(value = "/searchHosSqlTasks", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件进行his穿透查询", notes = "根据条件进行his穿透查询")
    public List<MHosSqlTask> searchHosSqlTasks(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        return hosSqlTaskClient.searchHosSqlTasks(fields,filters,sorts,size,page);
    }



    @RequestMapping(value = "/createHosSqlTask", method = RequestMethod.POST)
    @ApiOperation(value = "创建his穿透信息", notes = "创建his穿透信息")
    public MHosSqlTask createHosSqlTask(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data", required = true) String jsonData) throws Exception {
        return hosSqlTaskClient.createHosSqlTask(jsonData);
    }

    @RequestMapping(value = "/updateHosSqlTask", method = RequestMethod.PUT)
    @ApiOperation(value = "修改his穿透信息", notes = "修改his穿透信息")
    public MHosSqlTask updateHosSqlTask(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        return hosSqlTaskClient.updateHosSqlTask(jsonData);
    }


    @RequestMapping(value = "/deleteHosSqlTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除his穿透信息", notes = "删除his穿透信息")
    public boolean deleteHosSqlTask(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return hosSqlTaskClient.deleteHosSqlTask(id);
    }
}