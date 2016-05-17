package com.yihu.ehr.api.esb.controller;

import com.yihu.ehr.api.esb.model.HosEsbMiniInstallLog;
import com.yihu.ehr.api.esb.service.HosEsbMiniInstallLogService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.esb.MHosEsbMiniInstallLog;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class HosEsbMiniInstallLogController extends BaseRestController{

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


}