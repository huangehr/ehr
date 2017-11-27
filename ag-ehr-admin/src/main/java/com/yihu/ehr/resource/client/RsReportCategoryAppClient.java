package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wxw on 2017/11/24.
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsReportCategoryAppClient {
    @ApiOperation("根据报表分类编码和应用编码删除信息")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryAppDelete, method = RequestMethod.DELETE)
    Envelop deleteByCategoryIdAndAppId(
            @RequestParam(value = "reportCategoryId")String reportCategoryId,
            @RequestParam(value = "appId")String appId);

    @ApiOperation("根据报表分类编码和应用编码新增")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryAppSave, method = RequestMethod.POST)
    Envelop saveCategoryIdAndAppId(
            @RequestParam(value = "reportCategoryId")String reportCategoryId,
            @RequestParam(value = "appId")String appId);

    @ApiOperation("根据报表分类编码获取应用编码")
    @RequestMapping(value = ServiceApi.Resources.GetRsReportCategoryApps, method = RequestMethod.GET)
    String getAppIdByCategory(
            @RequestParam(value = "reportCategoryId")String reportCategoryId);
}
