package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsReportCategoryModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.resource.MRsReportCategory;
import com.yihu.ehr.resource.client.RsReportCategoryClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @ApiOperation(value = "根据条件，获取资源分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryTree, method = RequestMethod.GET)
    public Envelop getTreeData(
            @ApiParam(name = "codeName", value = "资源分类编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);

            // 获取最顶层的资源报表分类集合
            List<MRsReportCategory> topNodeList = rsReportCategoryClient.getChildrenByPid(-1);
            if (topNodeList.size() == 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("没有匹配条件的资源报表分类！");
                return envelop;
            }

            // 暂存最顶层资源报表分类中，满足条件的集合
            List<MRsReportCategory> topNodeListIn = new ArrayList<>();
            // 暂存最顶层资源报表分类中，不满足条件的集合
            List<MRsReportCategory> topNodeListOut = new ArrayList<>();
            // 暂存满足条件的结果集
            List<RsReportCategoryModel> resultList = new ArrayList<>();

            if (StringUtils.isEmpty(codeName)) {
                resultList = getTreeByParents(topNodeList);
                envelop.setDetailModelList(resultList);
                return envelop;
            }

            for (MRsReportCategory reportCategory : topNodeList) {
                if (reportCategory.getCode().contains(codeName) || reportCategory.getName().contains(codeName)) {
                    topNodeListIn.add(reportCategory);
                    continue;
                }
                topNodeListOut.add(reportCategory);
            }
            if (topNodeListIn.size() != 0) {
                resultList.addAll(getTreeByParents(topNodeListIn));
            }
            resultList.addAll(getTreeByParentsAndCodeName(topNodeListOut, codeName));

            envelop.setDetailModelList(resultList);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "获取资源分类树形下拉框数据")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryComboTree, method = RequestMethod.GET)
    public Envelop getComboTreeData(
            @ApiParam(name = "name", value = "资源报表分类名称")
            @RequestParam(value = "name", required = false) String name
    ) throws Exception {
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
            @ApiParam(name = "rsReportCategory", value = "报表分类JSON字符串", required = true)
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
            @ApiParam(name = "rsReportCategory", value = "报表分类JSON字符串", required = true)
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
            @PathVariable(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            rsReportCategoryClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportCategoryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    /**
     * 根据父级集合，递归获取父级及其自子级集合，形成树形结构
     *
     * @param parentList 父级集合
     * @return 父级及其子集的树形结构数据
     */
    private List<RsReportCategoryModel> getTreeByParents(List<MRsReportCategory> parentList) {
        List<RsReportCategoryModel> resultList = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            MRsReportCategory parent = parentList.get(i);

            List<MRsReportCategory> childList = rsReportCategoryClient.getChildrenByPid(parent.getId());
            List<RsReportCategoryModel> childModeList = getTreeByParents(childList);

            RsReportCategoryModel parentModel = convertToModel(parent, RsReportCategoryModel.class);
            parentModel.setChildren(childModeList);

            resultList.add(parentModel);
        }
        return resultList;
    }

    /**
     * 递归不满足条件的父级集合，获取其满足条件的子集，并返回子集及其父级的树形结构
     *
     * @param parents  不满足条件的父级集合
     * @param codeName 资源报表分类编码或名称
     * @return 满足条件的子集及其父级的树形结构
     */
    private List<RsReportCategoryModel> getTreeByParentsAndCodeName(List<MRsReportCategory> parents, String codeName) {
        List<RsReportCategoryModel> result = new ArrayList<>();
        for (MRsReportCategory parent : parents) {
            RsReportCategoryModel parentModel = convertToModel(parent, RsReportCategoryModel.class);
            Integer parentId = parent.getId();
            List<RsReportCategoryModel> childrenModel = new ArrayList<>();

            List<MRsReportCategory> children = rsReportCategoryClient.getChildrenByPid(parentId);
            if (children.size() == 0) continue;

            // 获取满足条件的子节点
            String filters = "pid=" + parentId + ";code?" + codeName + " g1;name?" + codeName + " g1;";
            List<MRsReportCategory> childrenIin = (List<MRsReportCategory>) rsReportCategoryClient.search(filters).getBody();
            if (childrenIin.size() != 0) {
                childrenModel.addAll(getTreeByParents(childrenIin));
            }
            // 递归不满足条件的子节点
            children.removeAll(childrenIin);
            if (children.size() != 0) {
                childrenModel.addAll(getTreeByParentsAndCodeName(children, codeName));
            }

            if (childrenModel.size() != 0) {
                parentModel.setChildren(childrenModel);
                result.add(parentModel);
            }
        }
        return result;
    }

}
