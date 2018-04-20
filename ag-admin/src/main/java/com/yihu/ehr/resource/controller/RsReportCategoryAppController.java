package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.resource.client.RsReportCategoryAppClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wxw on 2017/11/24.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "metadata", description = "资源报表分类应用服务接口", tags = {"资源管理-资源报表分类应用服务接口"})
public class RsReportCategoryAppController extends BaseController {
    @Autowired
    private RsReportCategoryAppClient rsReportCategoryAppClient;

    @ApiOperation("根据报表分类编码和应用编码删除信息")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryAppDelete, method = RequestMethod.DELETE)
    public Envelop deleteByCategoryIdAndAppId(
            @ApiParam(name = "reportCategoryId", value = "报表分类编码")
            @RequestParam(value = "reportCategoryId")String reportCategoryId,
            @ApiParam(name = "appId", value = "应用编码")
            @RequestParam(value = "appId")String appId) {
        Envelop envelop = rsReportCategoryAppClient.deleteByCategoryIdAndAppId(reportCategoryId, appId);
        return envelop;
    }

    @ApiOperation("根据报表分类编码和应用编码新增")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryAppSave, method = RequestMethod.POST)
    public Envelop saveCategoryIdAndAppId(
            @ApiParam(name = "reportCategoryId", value = "报表分类编码")
            @RequestParam(value = "reportCategoryId")String reportCategoryId,
            @ApiParam(name = "appId", value = "应用编码")
            @RequestParam(value = "appId")String appId) {
        Envelop envelop = rsReportCategoryAppClient.saveCategoryIdAndAppId(reportCategoryId, appId);
        return envelop;
    }

    @ApiOperation("根据报表分类编码获取应用编码")
    @RequestMapping(value = ServiceApi.Resources.GetRsReportCategoryApps, method = RequestMethod.GET)
    public String getAppIdByCategory(
            @ApiParam(name = "reportCategoryId", value = "报表分类编码")
            @RequestParam(value = "reportCategoryId")String reportCategoryId) {
        String apps = rsReportCategoryAppClient.getAppIdByCategory(reportCategoryId);
        return apps;
    }
}
