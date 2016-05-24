package com.yihu.ehr.api.esb.controller;

import com.yihu.ehr.api.esb.model.HosSqlTask;
import com.yihu.ehr.api.esb.service.HosSqlTaskService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.esb.MHosSqlTask;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/esb")
@RestController
@Api(value = "his穿透管理接口", description = "his穿透管理接口")
public class HosSqlTaskController extends BaseRestController{

    @Autowired
    private HosSqlTaskService hosSqlTaskService;


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
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<MHosSqlTask> hosSqlTasks = hosSqlTaskService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, hosSqlTaskService.getCount(filters), page, size);

        return (List<MHosSqlTask>) convertToModels(hosSqlTasks, new ArrayList<MHosSqlTask>(hosSqlTasks.size()), MHosSqlTask.class, fields);
    }

    @RequestMapping(value = "/hosSqlTask/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取his穿透信息",notes = "根据id获取his穿透信息")
    public MHosSqlTask getHosSqlTask(
            @ApiParam(name = "id",value ="",defaultValue = "")
            @PathVariable(value = "id") String id) throws  Exception{
        HosSqlTask hosSqlTask = hosSqlTaskService.retrieve(id);
        return convertToModel(hosSqlTask,MHosSqlTask.class);
    }

    @RequestMapping(value = "/createHosSqlTask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建his穿透信息", notes = "创建his穿透信息")
    public MHosSqlTask createHosSqlTask(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        HosSqlTask hosSqlTask = toEntity(jsonData, HosSqlTask.class);
        hosSqlTask.setStatus("0");
        hosSqlTask.setCreateTime(new Date());
        hosSqlTaskService.save(hosSqlTask);
        return convertToModel(hosSqlTask, MHosSqlTask.class, null);
    }

    @RequestMapping(value = "/updateHosSqlTask", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改his穿透信息", notes = "修改his穿透信息")
    public MHosSqlTask updateHosSqlTask(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {

        HosSqlTask hosSqlTask = toEntity(jsonData, HosSqlTask.class);
        hosSqlTaskService.save(hosSqlTask);
        return convertToModel(hosSqlTask, MHosSqlTask.class, null);
    }


    @RequestMapping(value = "/deleteHosSqlTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除his穿透信息", notes = "删除his穿透信息")
    public boolean deleteHosSqlTask(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        hosSqlTaskService.delete(id);
        return true;
    }
}