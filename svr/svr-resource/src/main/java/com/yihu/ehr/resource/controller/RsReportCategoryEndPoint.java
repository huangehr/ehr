package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsReportCategory;
import com.yihu.ehr.resource.model.RsReportCategory;
import com.yihu.ehr.resource.service.RsReportCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表分类 服务接口
 *
 * @author 张进军
 * @created 2017.8.8 20:32
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "资源报表分类服务接口")
public class RsReportCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsReportCategoryService rsReportCategoryService;

    @ApiOperation("根据ID获取资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategory, method = RequestMethod.GET)
    public MRsReportCategory getById(
            @ApiParam(name = "id", value = "id", required = true)
            @PathVariable("id") Integer id) throws Exception {
        return convertToModel(rsReportCategoryService.getById(id), MRsReportCategory.class);
    }

    @ApiOperation(value = "根据条件获取资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategories, method = RequestMethod.GET)
    List<MRsReportCategory> search(
            @ApiParam(name = "codeName", value = "资源分类编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) throws ParseException {
        List<MRsReportCategory> resultList = new ArrayList<>();

        // 获取最顶层的资源报表分类集合
        List<RsReportCategory> topNodeList = rsReportCategoryService.getChildrenByPid(-1);
        if (topNodeList.size() == 0) {
            return resultList;
        }

        // 暂存最顶层资源报表分类中，满足条件的集合
        List<RsReportCategory> topNodeListIn = new ArrayList<>();
        // 暂存最顶层资源报表分类中，不满足条件的集合
        List<RsReportCategory> topNodeListOut = new ArrayList<>();

        if (StringUtils.isEmpty(codeName)) {
            List<RsReportCategory> treeList = rsReportCategoryService.getTreeByParents(topNodeList);
            return (List<MRsReportCategory>) convertToModels(treeList, resultList, MRsReportCategory.class, "");
        }

        for (RsReportCategory reportCategory : topNodeList) {
            if (reportCategory.getCode().contains(codeName) || reportCategory.getName().contains(codeName)) {
                topNodeListIn.add(reportCategory);
                continue;
            }
            topNodeListOut.add(reportCategory);
        }
        if (topNodeListIn.size() != 0) {
            List<RsReportCategory> inList = rsReportCategoryService.getTreeByParents(topNodeListIn);
            resultList.addAll(convertToModels(inList, new ArrayList<MRsReportCategory>(), MRsReportCategory.class, ""));
        }
        List<RsReportCategory> outList = rsReportCategoryService.getTreeByParentsAndCodeName(topNodeListOut, codeName);
        resultList.addAll(convertToModels(outList, new ArrayList<MRsReportCategory>(), MRsReportCategory.class, ""));

        return resultList;
    }

    @ApiOperation(value = "获取资源报表分类的树形下拉框数据")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryComboTree, method = RequestMethod.GET)
    public List<MRsReportCategory> getComboTreeData(
//            @ApiParam(name = "name", value = "资源报表分类名称")
//            @RequestParam(value = "name", required = false) String name
    ) throws Exception {
        List<RsReportCategory> list = rsReportCategoryService.getComboTreeData("");
        return (List<MRsReportCategory>) convertToModels(list, new ArrayList<MRsReportCategory>(list.size()), MRsReportCategory.class, "");
    }

    @ApiOperation("新增资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategorySave, method = RequestMethod.POST)
    public MRsReportCategory add(
            @ApiParam(name = "rsReportCategory", value = "资源报表分类JSON", required = true)
            @RequestBody String rsReportCategory) throws Exception {
        RsReportCategory newRsReportCategory = toEntity(rsReportCategory, RsReportCategory.class);
        newRsReportCategory = rsReportCategoryService.save(newRsReportCategory);
        return convertToModel(newRsReportCategory, MRsReportCategory.class);
    }

    @ApiOperation("更新资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategorySave, method = RequestMethod.PUT)
    public MRsReportCategory update(
            @ApiParam(name = "rsReportCategory", value = "资源报表分类JSON", required = true)
            @RequestBody String rsReportCategory) throws Exception {
        RsReportCategory newRsReportCategory = toEntity(rsReportCategory, RsReportCategory.class);
        newRsReportCategory = rsReportCategoryService.save(newRsReportCategory);
        return convertToModel(newRsReportCategory, MRsReportCategory.class);
    }

    @ApiOperation("删除资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryDelete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id) throws Exception {
        rsReportCategoryService.delete(id);
    }

    @ApiOperation("验证资源报表分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryIsUniqueCode, method = RequestMethod.GET)
    public boolean isUniqueCode(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "code", value = "资源报表分类编码", required = true)
            @RequestParam("code") String code) throws Exception {
        return rsReportCategoryService.isUniqueCode(id, code);
    }

    @ApiOperation("验证资源报表分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryIsUniqueName, method = RequestMethod.GET)
    public boolean isUniqueName(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表分类名称", required = true)
            @RequestParam("name") String name) throws Exception {
        return rsReportCategoryService.isUniqueName(id, name);
    }

}
