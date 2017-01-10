package com.yihu.ehr.apps.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.model.AppApi;
import com.yihu.ehr.apps.service.AppApiService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.app.MAppApi;
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
@Api(value = "AppApi", description = "平台应用", tags = {"平台应用"})
public class AppApiEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppApiService appApiService;

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApi")
    public MAppApi createAppApi(
            @ApiParam(name = "appApi", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appApiJson) throws Exception {
        AppApi appApi = toEntity(appApiJson, AppApi.class);
        appApi = appApiService.createAppApi(appApi);
        return convertToModel(appApi, MAppApi.class);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi列表")
    public Collection<MAppApi> getAppApis(
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
            List<AppApi> appApiList = appApiService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, appApiService.getCount(filters), page, size);
            return convertToModels(appApiList, new ArrayList<>(appApiList.size()), MAppApi.class, fields);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新appApi")
    public MAppApi updateAppApi(
            @ApiParam(name = "appApi", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppApi appApi = toEntity(appJson, AppApi.class);
        if (appApiService.retrieve(appApi.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        appApiService.save(appApi);
        return convertToModel(appApi, MAppApi.class);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi")
    public MAppApi getAppApi(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        AppApi appApi = appApiService.retrieve(id);
        return convertToModel(appApi, MAppApi.class);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApi")
    public boolean deleteAppApi(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        appApiService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApisNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤App列表")
    public Collection<MAppApi> getAppApiNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters
    ) throws Exception {
        List<AppApi> appApiList =  appApiService.search(filters);
        return convertToModels(appApiList,new ArrayList<>(appApiList.size()),MAppApi.class, "");
    }
}
