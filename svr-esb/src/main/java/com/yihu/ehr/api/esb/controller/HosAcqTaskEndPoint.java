package com.yihu.ehr.api.esb.controller;

import com.yihu.ehr.api.esb.model.HosAcqTask;
import com.yihu.ehr.api.esb.service.HosAcqTaskService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.esb.MHosAcqTask;
import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/esb")
@RestController
@Api(value = "补采任务管理接口", description = "补采任务管理接口")
public class HosAcqTaskEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private HosAcqTaskService hosAcqTaskService;


    @RequestMapping(value = "/searchHosAcqTasks", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取补采任务信息", notes = "根据查询条件获取补采任务信息")
    public List<MHosAcqTask> searchHosAcqTasks(
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
            HttpServletResponse response) throws ParseException {
        List<HosAcqTask> hosAcqTasks = hosAcqTaskService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, hosAcqTaskService.getCount(filters), page, size);

        return (List<MHosAcqTask>) convertToModels(hosAcqTasks, new ArrayList<MHosAcqTask>(hosAcqTasks.size()), MHosAcqTask.class, fields);
    }

    @RequestMapping(value = "/hosAcqTask/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取补采任务信息",notes = "根据id获取补采任务信息")
    public MHosAcqTask getHosAcqTask(
            @ApiParam(name = "id",value ="",defaultValue = "")
            @PathVariable(value = "id") String id) throws  Exception{
        HosAcqTask hosAcqTask = hosAcqTaskService.retrieve(id);
        return convertToModel(hosAcqTask,MHosAcqTask.class);
    }


    @RequestMapping(value = "/createHosAcqTask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建补采任务信息", notes = "创建补采任务信息")
    public MHosAcqTask createHosAcqTask(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        HosAcqTask hosAcqTask = toEntity(jsonData, HosAcqTask.class);
        hosAcqTask.setStatus("0");
        hosAcqTaskService.save(hosAcqTask);
        return convertToModel(hosAcqTask, MHosAcqTask.class, null);

    }

    @RequestMapping(value = "/updateHosAcqTask", method = RequestMethod.PUT)
    @ApiOperation(value = "修改补采任务信息", notes = "修改补采任务信息")
    public MHosAcqTask updateHosAcqTask(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        HosAcqTask hosAcqTask = toEntity(jsonData, HosAcqTask.class);
        hosAcqTaskService.save(hosAcqTask);
        return convertToModel(hosAcqTask, MHosAcqTask.class, null);
    }


    @RequestMapping(value = "/deleteHosAcqTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除补采任务信息", notes = "删除补采任务信息")
    public boolean deleteHosAcqTask(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        hosAcqTaskService.delete(id);
        return true;
    }
}