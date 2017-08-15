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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "根据父级ID获取下级")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryChildrenByPid, method = RequestMethod.GET)
    public List<MRsReportCategory> getChildrenByPid(
            @ApiParam(name = "pid", value = "父级ID")
            @RequestParam Integer pid) throws Exception {
        List<RsReportCategory> children = rsReportCategoryService.getChildrenByPid(pid);
        return (List<MRsReportCategory>) convertToModels(children, new ArrayList<MRsReportCategory>(children.size()), MRsReportCategory.class, "");
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
