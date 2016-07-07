package com.yihu.ehr.apps.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.model.AppApiFeature;
import com.yihu.ehr.apps.service.AppApiFeatureService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.app.MAppApiFeature;
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
@Api(value = "AppApiFeature", description = "功能管理", tags = {"平台应用"})
public class AppApiFeatureEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppApiFeatureService AppApiFeatureService;

    @RequestMapping(value = ServiceApi.AppApiFeature.AppApiFeature, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApiFeature")
    public MAppApiFeature createAppApiFeature(
            @ApiParam(name = "AppApiFeature", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String AppApiFeatureJson) throws Exception {
        AppApiFeature AppApiFeature = toEntity(AppApiFeatureJson, AppApiFeature.class);
        AppApiFeature = AppApiFeatureService.createAppApiFeature(AppApiFeature);
        return convertToModel(AppApiFeature, MAppApiFeature.class);
    }

    @RequestMapping(value = ServiceApi.AppApiFeature.AppApiFeature, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiFeature列表")
    public Collection<MAppApiFeature> getAppApiFeatures(
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
            Page<AppApiFeature> appPages = AppApiFeatureService.getAppApiFeatureList(sorts, page, size);

            pagedResponse(request, response, appPages.getTotalElements(), page, size);
            return convertToModels(appPages.getContent(), new ArrayList<>(appPages.getNumber()), MAppApiFeature.class, fields);
        } else {
            List<AppApiFeature> AppApiFeatureList = AppApiFeatureService.search(fields, filters, sorts, page, size);

            pagedResponse(request, response, AppApiFeatureService.getCount(filters), page, size);
            return convertToModels(AppApiFeatureList, new ArrayList<>(AppApiFeatureList.size()), MAppApiFeature.class, fields);
        }
    }

    @RequestMapping(value = ServiceApi.AppApiFeature.AppApiFeature, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppApiFeature")
    public MAppApiFeature updateAppApiFeature(
            @ApiParam(name = "AppApiFeature", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppApiFeature AppApiFeature = toEntity(appJson, AppApiFeature.class);
        if (AppApiFeatureService.retrieve(AppApiFeature.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        AppApiFeatureService.save(AppApiFeature);
        return convertToModel(AppApiFeature, MAppApiFeature.class);
    }

    @RequestMapping(value = ServiceApi.AppApiFeature.AppApiFeature, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiFeature")
    public MApp getAppApiFeature(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) throws Exception {
        AppApiFeature AppApiFeature = AppApiFeatureService.retrieve(id);
        return convertToModel(AppApiFeature, MApp.class);
    }

    @RequestMapping(value = ServiceApi.AppApiFeature.DeleteAppApiFeature, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApiFeature")
    public boolean deleteAppApiFeature(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) throws Exception {
        AppApiFeatureService.delete(id);
        return true;
    }
}
