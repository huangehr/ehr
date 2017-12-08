package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.model.RsResourceCategory;
import com.yihu.ehr.resource.service.RsResourceCategoryService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by lyr on 2016/5/4.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsResourceCategoryEndPoint", description = "资源分类", tags = {"资源服务-资源分类"})
public class RsResourceCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsResourceCategoryService rsCategoryService;

    @RequestMapping(value = ServiceApi.Resources.CategoryUpdate, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("资源类别创建")
    public MRsCategory createRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestBody String resourceCategory) throws Exception {
        RsResourceCategory rsCategory = toEntity(resourceCategory, RsResourceCategory.class);
        rsCategory.setId(getObjectId(BizObject.ResourceCategory));
        rsCategoryService.createOrUpdRsCategory(rsCategory);

        return convertToModel(rsCategory, MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    public Envelop deleteResourceCategory(
            @ApiParam(name = "id", value = "资源类别ID", defaultValue = "string")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop json = new Envelop();
        try{
            rsCategoryService.deleteRsCategory(id);
            json.setSuccessFlg(true);
        }catch (Exception e){
            json.setSuccessFlg(false);
            json.setErrorMsg(e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = ServiceApi.Resources.CategoryUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("资源类别更新")
    public MRsCategory updateRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"id\":\"string\",\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestBody String resourceCategory) throws Exception {
        RsResourceCategory rsCategory = toEntity(resourceCategory, RsResourceCategory.class);
        rsCategoryService.createOrUpdRsCategory(rsCategory);

        return convertToModel(rsCategory, MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源类别")
    public MRsCategory getRsCategoryById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rsCategoryService.getRsCategoryById(id), MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.CategoriesByPid, method = RequestMethod.GET)
    @ApiOperation("根据pid获取资源类别列表")
    public List<MRsCategory> getRsCategoryByPid(
            @ApiParam(name="pid",value="pid",defaultValue = "")
            @RequestParam(value="pid",required = false) String pid) throws Exception {

        List<RsResourceCategory> categoryList = rsCategoryService.getRsCategoryByPid(pid);
        return (List<MRsCategory>) convertToModels(categoryList, new ArrayList<MRsCategory>(categoryList.size()), MRsCategory.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.CategoriesByCodeAndPid, method = RequestMethod.GET)
    @ApiOperation("根据pid和code获取资源类别列表")
    public List<MRsCategory> getRsCategoryByPidAndCode(
            @ApiParam(name = "code", value="编码类型", defaultValue = "")
            @RequestParam(value= "code") String code,
            @ApiParam(name = "pid", value = "上级id", defaultValue = "")
            @RequestParam(value="pid",required = false) String pid) throws Exception {
        List<RsResourceCategory> categoryList = rsCategoryService.findByCodeAndPid(code, pid);
        return (List<MRsCategory>) convertToModels(categoryList, new ArrayList<MRsCategory>(categoryList.size()), MRsCategory.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.CategoryExitSelfAndParent, method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    public List<MRsCategory> getCateTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception {
        List<RsResourceCategory> parentTypes = rsCategoryService.getRsCategoryByPid(id);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes,id+",");   //递归获取
        List<RsResourceCategory> cdaTypes = rsCategoryService.getCateTypeExcludeSelfAndChildren(childrenIds);
        return  (List<MRsCategory>)convertToModels(cdaTypes,new ArrayList<MRsCategory>(cdaTypes.size()),MRsCategory.class,"");
    }

    @RequestMapping(value = ServiceApi.Resources.CategoriesAll, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public List<MRsCategory> getAllCategories(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<RsResourceCategory> resources = rsCategoryService.search(filters);
        return (List<MRsCategory>) convertToModels(resources, new ArrayList<MRsCategory>(resources.size()), MRsCategory.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.CategoriesSearch, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public List<MRsCategory> getRsCategories(
            @ApiParam(name = "roleId", value = "角色编码")
            @RequestParam(value = "roleId") String roleId,
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsCategory> rsList;
        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            if(roleId.equals("*")) {
                Page<RsResourceCategory> rsCatePage = null;
                rsCatePage = rsCategoryService.getRsCategories(sorts, reducePage(page), size);
                total = rsCatePage.getTotalElements();
                rsList = convertToModels(rsCatePage.getContent(), new ArrayList<>(rsCatePage.getNumber()), MRsCategory.class, fields);
            }else {
                List<RsResourceCategory> rsCateList = null;
                rsCateList = rsCategoryService.findByCode("derived");
                total = rsCateList.size();
                rsList = convertToModels(rsCateList, new ArrayList<>(rsCateList.size()), MRsCategory.class, fields);
            }
        } else {
            List<RsResourceCategory> rsCateList = null;
            if(roleId.equals("*")) {
                rsCateList = rsCategoryService.search(fields, filters, sorts, page, size);
            }else {
                filters += "code=derived;";
                rsCateList = rsCategoryService.search(fields, filters, sorts, page, size);
            }
            total = rsCateList.size();
            rsList = convertToModels(rsCateList, new ArrayList<>(rsCateList.size()), MRsCategory.class, fields);
        }
        pagedResponse(request, response, total, page, size);
        return (List<MRsCategory>) rsList;
    }

    public String getChildIncludeSelfByParentsAndChildrenIds(List<RsResourceCategory> parentTypes,String childrenIds) {
        for (int i = 0; i < parentTypes.size(); i++) {
            RsResourceCategory typeInfo = parentTypes.get(i);
            childrenIds+=typeInfo.getId()+",";
            List<RsResourceCategory> listChild = rsCategoryService.getRsCategoryByPid(typeInfo.getId());
            if(listChild.size()>0){
                childrenIds = getChildIncludeSelfByParentsAndChildrenIds(listChild,childrenIds);
            }
        }
        return childrenIds;
    }

}
