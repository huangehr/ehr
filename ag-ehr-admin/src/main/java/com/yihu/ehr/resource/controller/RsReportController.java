package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.resource.MRsReport;
import com.yihu.ehr.resource.client.RsReportClient;
import com.yihu.ehr.users.service.RoleReportRelationClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源报表 controller
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "RsReportController", description = "资源报表服务接口", tags = {"资源管理-资源报表服务接口"})
public class RsReportController extends BaseController {

    @Autowired
    private RsReportClient rsReportClient;
    @Autowired
    private RoleReportRelationClient roleReportRelationClient;

    @ApiOperation("根据ID获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReport, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") Integer id) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRsReport mRsReport = rsReportClient.getById(id);
            envelop.setObj(mRsReport);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("根据编码获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportFindByCode, method = RequestMethod.GET)
    public Envelop findByCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code") String code) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRsReport mRsReport = rsReportClient.findByCode(code);
            envelop.setObj(mRsReport);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "根据条件获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReports, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        Envelop envelop = new Envelop();
        try {
            ResponseEntity<List<MRsReport>> responseEntity = rsReportClient.search(fields, filters, sorts, page, size);
            List<MRsReport> mRsReportList = responseEntity.getBody();
            envelop = getResult(mRsReportList, getTotalCount(responseEntity), page, size);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("新增资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportSave, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "rsReport", value = "资源报表JSON字符串", required = true)
            @RequestParam(value = "rsReport") String rsReport) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsReport newMRsReport = rsReportClient.add(rsReport);
            envelop.setObj(newMRsReport);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("更新资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportSave, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "rsReport", value = "资源报表JSON字符串", required = true)
            @RequestParam(value = "rsReport") String rsReport) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsReport newMRsReport = rsReportClient.update(rsReport);
            envelop.setObj(newMRsReport);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportDelete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean isReportAccredited = roleReportRelationClient.isReportAccredited(id);
            if(isReportAccredited) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("该资源报表已被授权，不能删除。");
                return envelop;
            }

            rsReportClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证资源报表编码是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "code", value = "资源报表编码", required = true)
            @RequestParam("code") String code) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = rsReportClient.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该编码已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证资源报表名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表名称", required = true)
            @RequestParam("name") String name) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = rsReportClient.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该名称已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("获取报表模版内容")
    @RequestMapping(value = ServiceApi.Resources.RsReportTemplateContent, method = RequestMethod.GET)
    public Envelop getTemplateContent(
            @ApiParam(name = "reportCode", value = "资源报表Code", required = true)
            @RequestParam("reportCode") String reportCode) throws Exception {
        Envelop envelop = new Envelop();
        try {
            String templateContent = rsReportClient.getTemplateContent(reportCode);
            envelop.setObj(templateContent);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

}
