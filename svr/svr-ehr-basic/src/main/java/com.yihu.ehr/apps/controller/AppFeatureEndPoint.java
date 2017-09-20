package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.model.AppFeature;
import com.yihu.ehr.apps.service.AppFeatureService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MAppFeature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:04:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AppFeature", description = "功能管理", tags = {"平台应用-功能管理"})
public class AppFeatureEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppFeatureService appFeatureService;

    @RequestMapping(value = ServiceApi.AppFeature.AppFeature, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature")
    public MAppFeature getAppFeature(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        AppFeature appFeature = appFeatureService.retrieve(id);
        return convertToModel(appFeature, MAppFeature.class);
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeatures, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature列表")
    public Collection<MAppFeature> getAppFeatures(
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
        List<AppFeature> appFeatureList = appFeatureService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, appFeatureService.getCount(filters), page, size);
        return convertToModels(appFeatureList, new ArrayList<>(appFeatureList.size()), MAppFeature.class, fields);
    }

    @RequestMapping(value = ServiceApi.AppFeature.FilterFeatureList, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤AppFeature列表")
    public Boolean getAppFeaturesFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Long count = appFeatureService.getCount(filters);
        return count > 0 ? true : false;
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeatures, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppFeature")
    public MAppFeature createAppFeature(
            @ApiParam(name = "AppFeature", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String AppFeatureJson) throws Exception {
        AppFeature appFeature = toEntity(AppFeatureJson, AppFeature.class);
        appFeature = appFeatureService.createAppFeature(appFeature);
        // 拼接菜单JSON对象字符串，并保存
        appFeature = appFeatureService.joinMenuItemJsonStr(appFeature);
        appFeatureService.save(appFeature);

        return convertToModel(appFeature, MAppFeature.class);
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeatures, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppFeature")
    public MAppFeature updateAppFeature(
            @ApiParam(name = "AppFeature", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppFeature appFeature = toEntity(appJson, AppFeature.class);
        if (appFeatureService.retrieve(appFeature.getId()) == null)
            throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        // 拼接菜单JSON对象字符串
        appFeature = appFeatureService.joinMenuItemJsonStr(appFeature);
        appFeatureService.save(appFeature);
        return convertToModel(appFeature, MAppFeature.class);
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeature, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppFeature")
    public boolean deleteAppFeature(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        appFeatureService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.AppFeature.FilterFeatureNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤App列表")
    public Collection<MAppFeature> getAppFeatureNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<AppFeature> appFeatureList = appFeatureService.search(filters);
        return convertToModels(appFeatureList, new ArrayList<>(appFeatureList.size()), MAppFeature.class, "");
    }
}
