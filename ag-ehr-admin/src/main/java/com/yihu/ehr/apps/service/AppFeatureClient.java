package com.yihu.ehr.apps.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MAppFeature;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by linz on 2016年7月8日11:30:03.
 */
@FeignClient(name = MicroServices.Application)
public interface AppFeatureClient {

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeatures, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature列表")
    ResponseEntity<List<MAppFeature>> getAppFeatures(
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

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeatures, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppFeature")
    MAppFeature createAppFeature(
            @ApiParam(name = "appFeature", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestBody String appFeatureJson);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeature, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature")
    MAppFeature getAppFeature(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeatures, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppFeature")
    MAppFeature updateAppFeature(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeature, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppFeature")
    boolean deleteAppFeature(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.FilterFeatureList, method = RequestMethod.GET)
    @ApiOperation(value = "存在性校验")
    boolean isExitAppFeature(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.FilterFeatureNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature列表（不分页）")
    public Collection<MAppFeature> getAppFeatureNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.FilterFeatureNoPageSorts, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature排序后的列表（不分页）")
    public Collection<MAppFeature> getAppFeatureNoPageSorts(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.FindAppMenus, method = RequestMethod.GET)
    @ApiOperation(value = "根据权限，获取应用菜单")
    public List<Map<String, Object>> findAppMenus(
            @ApiParam(name = "appId", value = "应用ID", required = true)
            @RequestParam(value = "appId", required = true) String appId,
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam(value = "userId", required = true) String userId);
}
