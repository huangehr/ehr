package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.resource.MRsReportView;
import com.yihu.ehr.resource.client.RsReportViewClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资源报表视图配置 controller
 *
 * @author 张进军
 * @created 2017.8.22 14:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "metadata", description = "资源报表视图配置服务接口", tags = {"资源管理-资源报表视图配置服务接口"})
public class RsReportViewController extends BaseController {

    @Autowired
    private RsReportViewClient rsReportViewClient;

    @ApiOperation(value = "根据资源报表ID，获取资源报表视图配置")
    @RequestMapping(value = ServiceApi.Resources.RsReportViews, method = RequestMethod.GET)
    public Envelop findByReportId(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") int reportId) throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsReportView> mrsReportList = rsReportViewClient.findByReportId(reportId);
            envelop.setDetailModelList(mrsReportList);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportViewController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("保存资源报表视图配置")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewSave, method = RequestMethod.POST)
    public Envelop save(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") Integer reportId,
            @ApiParam(name = "modelListJson", value = "资源报表视图配置集合JSON字符串", required = true)
            @RequestParam(value = "modelListJson") String modelListJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            rsReportViewClient.save(reportId, modelListJson);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportViewController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("判断资源报表视图配置是否存在")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewExist, method = RequestMethod.GET)
    public Envelop exist(
            @ApiParam(name = "reportId", value = "资源报表ID", required = true)
            @RequestParam(value = "reportId") Integer reportId,
            @ApiParam(name = "resourceId", value = "视图ID", required = true)
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean isExist = rsReportViewClient.exist(reportId, resourceId);
            envelop.setObj(isExist);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportViewController.class).error(e.getMessage());
            return failed(e.getMessage());
        }
    }

    @ApiOperation("判断资源视图是否存在于报表中")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewExistReport, method = RequestMethod.GET)
    public Envelop resourceViewExitReport(
            @ApiParam(name = "resourceId", value = "视图ID", required = true)
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean isExist = rsReportViewClient.existReport(resourceId);
            envelop.setSuccessFlg(isExist);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportViewController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("判断资源报表是否关联相关资源")
    @RequestMapping(value = ServiceApi.Resources.RsReportViewExistByResourceId, method = RequestMethod.GET)
    public Envelop existByResourceId(
            @ApiParam(name = "resourceId", value = "视图ID", required = true)
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
         return rsReportViewClient.existByResourceId(resourceId);
    }
}
