package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsReportView;
import com.yihu.ehr.resource.model.RsReportView;
import com.yihu.ehr.resource.service.RsReportViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表视图配置 服务接口
 *
 * @author 张进军
 * @created 2017.8.22 14:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "资源报表视图配置服务接口")
public class RsReportViewEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsReportViewService rsReportViewService;

    @ApiOperation(value = "根据资源报表ID，获取资源报表视图配置")
    @RequestMapping(value = ServiceApi.Resources.RsReportViews, method = RequestMethod.GET)
    List<MRsReportView> findByReportId(
            @ApiParam(name = "reportId", value = "资源报表ID")
            @RequestParam(value = "reportId") Integer reportId) throws Exception {
        List<RsReportView> rsReportViews = rsReportViewService.findByReportId(reportId);
        return (List<MRsReportView>) convertToModels(rsReportViews, new ArrayList<MRsReportView>(), MRsReportView.class, "");
    }

    @ApiOperation("保存资源报表视图配置")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewSave, method = RequestMethod.POST)
    public void save(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") Integer reportId,
            @ApiParam(name = "modelListJson", value = "资源报表视图配置集合JSON字符串", required = true)
            @RequestParam(value = "modelListJson") String modelListJson) throws Exception {
        List<RsReportView> rsReportViewList = objectMapper.readValue(modelListJson, new TypeReference<List<RsReportView>>(){});
        rsReportViewService.save(reportId, rsReportViewList);
    }

    @ApiOperation("判断资源报表视图配置是否存在")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewExist, method = RequestMethod.GET)
    public boolean exist(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") Integer reportId,
            @ApiParam(name = "resourceId", value = "视图ID", required = true)
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        RsReportView rsReportView = rsReportViewService.findByReportIdAndResourceId(reportId, resourceId);
        if (rsReportView != null) {
            return true;
        }
        return false;
    }

}
