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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private AppApiParameterService AppApiParameterService;

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApiParameter")
    public MAppApiParameter createAppApiParameter(
            @ApiParam(name = "AppApiParameter", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String AppApiParameterJson) throws Exception {
        AppApiParameter AppApiParameter = toEntity(AppApiParameterJson, AppApiParameter.class);
        AppApiParameter = AppApiParameterService.createAppApiParameter(AppApiParameter);
        return convertToModel(AppApiParameter, MAppApiParameter.class);
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
        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<AppApiParameter> appPages = AppApiParameterService.getAppApiParameterList(sorts, page, size);

            pagedResponse(request, response, appPages.getTotalElements(), page, size);
            return convertToModels(appPages.getContent(), new ArrayList<>(appPages.getNumber()), MAppApiParameter.class, fields);
        } else {
            List<AppApiParameter> AppApiParameterList = AppApiParameterService.search(fields, filters, sorts, page, size);

            pagedResponse(request, response, AppApiParameterService.getCount(filters), page, size);
            return convertToModels(AppApiParameterList, new ArrayList<>(AppApiParameterList.size()), MAppApiParameter.class, fields);
        }
    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppApiParameter")
    public MAppApiParameter updateAppApiParameter(
            @ApiParam(name = "AppApiParameter", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppApiParameter AppApiParameter = toEntity(appJson, AppApiParameter.class);
        if (AppApiParameterService.retrieve(AppApiParameter.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        AppApiParameterService.save(AppApiParameter);
        return convertToModel(AppApiParameter, MAppApiParameter.class);
    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameter, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiParameter")
    public MAppApiParameter getAppApiParameter(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) throws Exception {
        AppApiParameter AppApiParameter = AppApiParameterService.retrieve(id);
        return convertToModel(AppApiParameter, MAppApiParameter.class);
    }

    @RequestMapping(value = ServiceApi.AppApiParameter.AppApiParameter, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApiParameter")
    public boolean deleteAppApiParameter(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) throws Exception {
        AppApiParameterService.delete(id);
        return true;
    }
}
