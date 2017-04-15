package com.yihu.ehr.apps.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.model.AppApiResponse;
import com.yihu.ehr.apps.service.AppApiResponseService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MAppApiResponse;
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
@Api(value = "AppApiResponse", description = "平台应用响应参数", tags = {"平台应用-平台应用响应参数"})
public class AppApiResponseEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppApiResponseService AppApiResponseService;

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApiResponse")
    public MAppApiResponse createAppApiResponse(
            @ApiParam(name = "AppApiResponse", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appApiResponseJson) throws Exception {
        AppApiResponse appApiResponse = toEntity(appApiResponseJson, AppApiResponse.class);
        appApiResponse = AppApiResponseService.createAppApiResponse(appApiResponse);
        return convertToModel(appApiResponse, MAppApiResponse.class);
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiResponse列表")
    public Collection<MAppApiResponse> getAppApiResponses(
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

            List<AppApiResponse> appApiResponseList = AppApiResponseService.search(fields, filters, sorts, page, size);

            pagedResponse(request, response, AppApiResponseService.getCount(filters), page, size);
            return convertToModels(appApiResponseList, new ArrayList<>(appApiResponseList.size()), MAppApiResponse.class, fields);

    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppApiResponse")
    public MAppApiResponse updateAppApiResponse(
            @ApiParam(name = "AppApiResponse", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppApiResponse appApiResponse = toEntity(appJson, AppApiResponse.class);
        if (AppApiResponseService.retrieve(appApiResponse.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        AppApiResponseService.save(appApiResponse);
        return convertToModel(appApiResponse, MAppApiResponse.class);
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponse, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiResponse")
    public MAppApiResponse getAppApiResponse(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        AppApiResponse appApiResponse = AppApiResponseService.retrieve(id);
        return convertToModel(appApiResponse, MAppApiResponse.class);
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponse, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApiResponse")
    public boolean deleteAppApiResponse(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        AppApiResponseService.delete(id);
        return true;
    }
}
