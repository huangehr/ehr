package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MRsReportView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 资源报表视图配置 client
 *
 * @author 张进军
 * @created 2017.8.22 14:05
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsReportViewClient {

    @ApiOperation(value = "根据资源报表ID，获取资源报表视图配置")
    @RequestMapping(value = ServiceApi.Resources.RsReportViews, method = RequestMethod.GET)
    List<MRsReportView> findByReportId(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") int reportId);

    @ApiOperation("保存资源报表视图配置")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewSave, method = RequestMethod.POST)
    void save(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") Integer reportId,
            @ApiParam(name = "modelListJson", value = "资源报表视图配置集合JSON字符串", required = true)
            @RequestParam(value = "modelListJson") String modelListJson);

    @ApiOperation("判断资源报表视图配置是否存在")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewExist, method = RequestMethod.GET)
    boolean exist(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") Integer reportId,
            @ApiParam(name = "resourceId", value = "视图ID", required = true)
            @RequestParam(value = "resourceId") String resourceId);

}
