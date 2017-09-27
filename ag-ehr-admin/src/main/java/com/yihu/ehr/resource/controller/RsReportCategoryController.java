package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.resource.MRsReportCategory;
import com.yihu.ehr.resource.client.RsReportCategoryClient;
import com.yihu.ehr.resource.client.RsReportClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源报表分类 controller
 *
 * @author 张进军
 * @created 2017.8.8 19:22
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "metadata", description = "资源报表分类服务接口", tags = {"资源管理-资源报表分类服务接口"})
public class RsReportCategoryController extends BaseController {

    @Autowired
    private RsReportCategoryClient rsReportCategoryClient;
    @Autowired
    private RsReportClient rsReportClient;

    @ApiOperation("根据ID获取资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategory, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") Integer id) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRsReportCategory mRsReportCategory = rsReportCategoryClient.getById(id);
            envelop.setObj(mRsReportCategory);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "根据条件，获取资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryTree, method = RequestMethod.GET)
    public Envelop getTreeData(
            @ApiParam(name = "codeName", value = "资源报表分类编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            List<MRsReportCategory> resultList = rsReportCategoryClient.search(codeName);
            envelop.setDetailModelList(resultList);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "获取资源报表分类树形下拉框数据")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryComboTree, method = RequestMethod.GET)
    public Envelop getComboTreeData() throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            List<MRsReportCategory> treeList = rsReportCategoryClient.getComboTreeData();
            envelop.setDetailModelList(treeList);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("新增资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategorySave, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "rsReportCategory", value = "资源报表分类JSON字符串", required = true)
            @RequestParam(value = "rsReportCategory") String rsReportCategory) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsReportCategory newMRsReportCategory = rsReportCategoryClient.add(rsReportCategory);
            envelop.setObj(newMRsReportCategory);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("更新资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategorySave, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "rsReportCategory", value = "资源报表分类JSON字符串", required = true)
            @RequestParam(value = "rsReportCategory") String rsReportCategory) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRsReportCategory newMRsReportCategory = rsReportCategoryClient.update(rsReportCategory);
            envelop.setObj(newMRsReportCategory);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryDelete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean isCategoryApplied = rsReportClient.isCategoryApplied(id);
            if(isCategoryApplied) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("该资源报表分类已经被应用，不能删除。");
                return envelop;
            }

            rsReportCategoryClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证资源报表分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryIsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "code", value = "资源报表分类编码", required = true)
            @RequestParam("code") String code) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = rsReportCategoryClient.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该编码已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证资源报表分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryIsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表分类名称", required = true)
            @RequestParam("name") String name) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = rsReportCategoryClient.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该名称已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

}
