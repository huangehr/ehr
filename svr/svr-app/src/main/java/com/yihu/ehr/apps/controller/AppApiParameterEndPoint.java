package com.yihu.ehr.apps.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.model.AppApiParameter;
import com.yihu.ehr.apps.service.AppApiParameterService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MAppApiParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:04:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AppApiParameter", description = "API应用请求参数", tags = {"平台应用"})
public class AppApiParameterEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppApiParameterService appApiParameterService;

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApiParameter")
    public MAppApiParameter createAppApiParameter(
            @ApiParam(name = "AppApiParameter", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appApiParameterJson) throws Exception {
        AppApiParameter appApiParameter = toEntity(appApiParameterJson, AppApiParameter.class);
        appApiParameter = appApiParameterService.createAppApiParameter(appApiParameter);
        return convertToModel(appApiParameter, MAppApiParameter.class);
    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiParameter列表")
    public Collection<MAppApiParameter> getAppApiParameters(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
            List<AppApiParameter> appApiParameterList = appApiParameterService.search(fields, filters, sorts, page, size);

            pagedResponse(request, response, appApiParameterService.getCount(filters), page, size);
            return convertToModels(appApiParameterList, new ArrayList<>(appApiParameterList.size()), MAppApiParameter.class, fields);

    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppApiParameter")
    public MAppApiParameter updateAppApiParameter(
            @ApiParam(name = "AppApiParameter", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppApiParameter appApiParameter = toEntity(appJson, AppApiParameter.class);
        if (appApiParameterService.retrieve(appApiParameter.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        appApiParameterService.save(appApiParameter);
        return convertToModel(appApiParameter, MAppApiParameter.class);
    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameter, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiParameter")
    public MAppApiParameter getAppApiParameter(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        AppApiParameter appApiParameter = appApiParameterService.retrieve(id);
        return convertToModel(appApiParameter, MAppApiParameter.class);
    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameter, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApiParameter")
    public boolean deleteAppApiParameter(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        appApiParameterService.delete(id);
        return true;
    }
}
