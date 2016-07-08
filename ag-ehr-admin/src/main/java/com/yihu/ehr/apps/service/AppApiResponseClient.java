package com.yihu.ehr.apps.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MAppApiResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by linz on 2016年7月8日11:30:03.
 */
@FeignClient(name=MicroServices.Application)
public interface AppApiResponseClient {

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiResponse列表")
    ResponseEntity<List<MAppApiResponse>> getAppApiResponses(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApiResponse")
    MAppApiResponse createAppApiResponse(
            @ApiParam(name = "AppApiResponse", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestBody String AppApiResponseJson);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiResponse.AppApiResponse, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiResponse")
    MAppApiResponse getAppApiResponse(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppApiResponse")
    MAppApiResponse updateAppApiResponse(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiResponse.AppApiResponse, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppFeature")
    boolean deleteAppApiResponse(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id);
}
