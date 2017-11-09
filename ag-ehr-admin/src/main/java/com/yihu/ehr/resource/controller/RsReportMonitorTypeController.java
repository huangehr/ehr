package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.resource.MRsReportMonitorType;
import com.yihu.ehr.resource.client.RsReportMonitorTypeClient;
import com.yihu.ehr.resource.client.RsReportClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 资源报表监测监测分类 controller
 *
 * @author janseny
 * @created 2017年11月7日15:08:16
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "rsReportMonitorType", description = "资源报表监测分类服务接口", tags = {"资源管理-资源报表监测分类服务接口"})
public class RsReportMonitorTypeController extends BaseController {

    @Autowired
    private RsReportMonitorTypeClient rsReportMonitorTypeClient;


    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypes, method = RequestMethod.GET)
    @ApiOperation(value = "资源报表监测分类列表分页", notes = "资源报表监测分类列表")
    public Envelop searchRsReportMonitorTypes(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,note")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ListResult listResult = rsReportMonitorTypeClient.search(fields, filters, size, page);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @ApiOperation("根据ID获取资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorType, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") Integer id) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRsReportMonitorType mRsReportMonitorType = rsReportMonitorTypeClient.getById(id);
            envelop.setObj(mRsReportMonitorType);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RsReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("新增资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeSave, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON字符串", required = true)
            @RequestParam(value = "rsReportMonitorType") String rsReportMonitorType) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsReportMonitorType newMRsReportMonitorType = rsReportMonitorTypeClient.add(rsReportMonitorType);
            envelop.setObj(newMRsReportMonitorType);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("更新资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeSave, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON字符串", required = true)
            @RequestParam(value = "rsReportMonitorType") String rsReportMonitorType) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsReportMonitorType newMRsReportMonitorType = rsReportMonitorTypeClient.update(rsReportMonitorType);
            envelop.setObj(newMRsReportMonitorType);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeDelete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            rsReportMonitorTypeClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证资源报表监测分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeIsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "资源报表监测分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表监测分类名称", required = true)
            @RequestParam("name") String name) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = rsReportMonitorTypeClient.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该名称已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypesNoPage, method = RequestMethod.GET)
    @ApiOperation("获取资源报表监测类别")
    public List<MRsReportMonitorType> getAllCategories(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
       return  rsReportMonitorTypeClient.getAll(filters);
    }

}
