package com.yihu.ehr.api.esb.controller;

import com.yihu.ehr.api.esb.model.HosEsbMiniInstallLog;
import com.yihu.ehr.api.esb.service.HosEsbMiniInstallLogService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.esb.MHosEsbMiniInstallLog;
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
@Api(value = "安装日志管理接口", description = "安装日志管理接口")
public class HosEsbMiniInstallLogEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private HosEsbMiniInstallLogService hosEsbMiniInstallLogService;

    @RequestMapping(value = "/searchInstallLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取安装日志信息", notes = "根据查询条件获取安装日志信息")
    public List<MHosEsbMiniInstallLog> searchInstallLogs(
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
        List<HosEsbMiniInstallLog> hosEsbMiniInstallLogs = hosEsbMiniInstallLogService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, hosEsbMiniInstallLogService.getCount(filters), page, size);

        return (List<MHosEsbMiniInstallLog>) convertToModels(hosEsbMiniInstallLogs, new ArrayList<MHosEsbMiniInstallLog>(hosEsbMiniInstallLogs.size()), MHosEsbMiniInstallLog.class, fields);
    }


    @RequestMapping(value = "/createInstallLog", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建安装日志信息", notes = "创建安装日志信息")
    public MHosEsbMiniInstallLog createInstallLog(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        HosEsbMiniInstallLog hosEsbMiniInstallLog = toEntity(jsonData, HosEsbMiniInstallLog.class);

        hosEsbMiniInstallLogService.save(hosEsbMiniInstallLog);
        return convertToModel(hosEsbMiniInstallLog, MHosEsbMiniInstallLog.class, null);
    }

    @RequestMapping(value = "/updateInstallLog", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改安装日志信息", notes = "修改安装日志信息")
    public MHosEsbMiniInstallLog updateInstallLog(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {

        HosEsbMiniInstallLog hosEsbMiniInstallLog = toEntity(jsonData, HosEsbMiniInstallLog.class);
        hosEsbMiniInstallLogService.save(hosEsbMiniInstallLog);
        return convertToModel(hosEsbMiniInstallLog, MHosEsbMiniInstallLog.class, null);
    }


    @RequestMapping(value = "/deleteInstallLog/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除安装日志信息", notes = "删除安装日志信息")
    public boolean deleteInstallLog(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        hosEsbMiniInstallLogService.delete(id);
        return true;
    }


    @RequestMapping(value = "/deleteInstallLogs", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据查询条件批量删除安装日志信息", notes = "根据查询条件批量删除安装日志信息")
    public boolean deleteInstallLogs(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws ParseException {
        List<HosEsbMiniInstallLog> hosEsbMiniInstallLogs = hosEsbMiniInstallLogService.search( filters);
        for(HosEsbMiniInstallLog hosEsbMiniInstallLog : hosEsbMiniInstallLogs){
            hosEsbMiniInstallLogService.delete(hosEsbMiniInstallLog.getId());
        }
        return true;
    }


}