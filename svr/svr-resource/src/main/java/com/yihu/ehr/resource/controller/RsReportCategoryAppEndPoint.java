package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.ReportCategoryAppRelation;
import com.yihu.ehr.resource.service.RsReportCategoryAppService;
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
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsReportCategoryApp", description = "资源报表分类与应用服务接口")
public class RsReportCategoryAppEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsReportCategoryAppService rsReportCategoryAppService;

    @ApiOperation("根据报表分类编码和应用编码删除信息")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryAppDelete, method = RequestMethod.DELETE)
    public Envelop deleteByCategoryIdAndAppId(
            @ApiParam(name = "reportCategoryId", value = "报表分类编码")
            @RequestParam(value = "reportCategoryId")String reportCategoryId,
            @ApiParam(name = "appId", value = "应用编码")
            @RequestParam(value = "appId")String appId) {
        Envelop envelop = new Envelop();
        try {
            rsReportCategoryAppService.deleteByCategoryIdAndAppId(reportCategoryId, appId);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("根据报表分类编码和应用编码新增")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryAppSave, method = RequestMethod.POST)
    public Envelop saveCategoryIdAndAppId(
            @ApiParam(name = "reportCategoryId", value = "报表分类编码")
            @RequestParam(value = "reportCategoryId")String reportCategoryId,
            @ApiParam(name = "appId", value = "应用编码")
            @RequestParam(value = "appId")String appId) {
        Envelop envelop = new Envelop();
        try {
            ReportCategoryAppRelation reportCategoryAppRelation = rsReportCategoryAppService.saveInfo(reportCategoryId, appId);
            if (null != reportCategoryAppRelation) {
                envelop.setSuccessFlg(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("根据报表分类编码获取应用编码")
    @RequestMapping(value = ServiceApi.Resources.GetRsReportCategoryApps, method = RequestMethod.GET)
    public String getAppIdByCategory(
            @ApiParam(name = "reportCategoryId", value = "报表分类编码")
            @RequestParam(value = "reportCategoryId")String reportCategoryId) {
        String appIds = rsReportCategoryAppService.getAppIdByCategory(reportCategoryId);
        return appIds;
    }
}
