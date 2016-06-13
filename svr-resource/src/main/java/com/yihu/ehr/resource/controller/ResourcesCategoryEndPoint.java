package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.service.ResourcesCategoryService;
import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
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
@Api(value = "resourceCategory", description = "资源分类服务接口")
public class ResourcesCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourcesCategoryService rsCategoryService;

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("资源类别创建")
    public MRsCategory createRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestBody String resourceCategory) throws Exception {
        RsCategory rsCategory = toEntity(resourceCategory, RsCategory.class);
        rsCategory.setId(getObjectId(BizObject.ResourceCategory));
        rsCategoryService.createOrUpdRsCategory(rsCategory);

        return convertToModel(rsCategory, MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("资源类别更新")
    public MRsCategory updateRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"id\":\"string\",\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestBody String resourceCategory) throws Exception {
        RsCategory rsCategory = toEntity(resourceCategory, RsCategory.class);
        rsCategoryService.createOrUpdRsCategory(rsCategory);

        return convertToModel(rsCategory, MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    public JSONObject deleteResourceCategory(
            @ApiParam(name = "id", value = "资源类别ID", defaultValue = "string")
            @PathVariable(value = "id") String id) throws Exception {
        JSONObject  json = new JSONObject();
        try{
            rsCategoryService.deleteRsCategory(id);
            json.put("successFlg",true);
            json.put("msg","删除成功！");
        }catch (Exception e){
            json.put("successFlg",false);
            json.put("msg",e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源类别")
    public MRsCategory getRsCategoryById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rsCategoryService.getRsCategoryById(id), MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.CategoryByPid,method = RequestMethod.GET)
    @ApiOperation("根据pid获取资源类别列表")
    public List<MRsCategory> getRsCategoryByPid(
            @ApiParam(name="pid",value="pid",defaultValue = "")
            @RequestParam(value="pid",required = false) String pid) throws Exception {

        List<RsCategory> categoryList;
        categoryList = rsCategoryService.getRsCategoryByPid(pid);
        return (List<MRsCategory>) convertToModels(categoryList, new ArrayList<MRsCategory>(categoryList.size()), MRsCategory.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public List<MRsCategory> getRsCategories(
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
            HttpServletResponse response
    ) throws Exception {
        long total = 0;
        Collection<MRsCategory> rsList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsCategory> resources = rsCategoryService.getRsCategories(sorts, reducePage(page), size);
            total = resources.getTotalElements();
            rsList = convertToModels(resources.getContent(), new ArrayList<>(resources.getNumber()), MRsCategory.class, fields);
        } else {
            List<RsCategory> resources = rsCategoryService.search(fields, filters, sorts, page, size);
            total = rsCategoryService.getCount(filters);
            rsList = convertToModels(resources, new ArrayList<>(resources.size()), MRsCategory.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsCategory>) rsList;
    }

    @RequestMapping(value = ServiceApi.Resources.NoPageCategories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public List<MRsCategory> getAllCategories(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<RsCategory> resources = rsCategoryService.search(filters);
        return (List<MRsCategory>) convertToModels(resources, new ArrayList<MRsCategory>(resources.size()), MRsCategory.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.CategoryExitSelfAndChild, method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    public List<MRsCategory> getCateTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception {
        List<RsCategory> parentTypes = rsCategoryService.getRsCategoryByPid(id);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(parentTypes,id+",");   //递归获取
        List<RsCategory> cdaTypes = rsCategoryService.getCateTypeExcludeSelfAndChildren(childrenIds);
        return  (List<MRsCategory>)convertToModels(cdaTypes,new ArrayList<MRsCategory>(cdaTypes.size()),MRsCategory.class,"");
    }

    public String getChildIncludeSelfByParentsAndChildrenIds(List<RsCategory> parentTypes,String childrenIds) {
        for (int i = 0; i < parentTypes.size(); i++) {
            RsCategory typeInfo = parentTypes.get(i);
            childrenIds+=typeInfo.getId()+",";
            List<RsCategory> listChild = rsCategoryService.getRsCategoryByPid(typeInfo.getId());
            if(listChild.size()>0){
                childrenIds = getChildIncludeSelfByParentsAndChildrenIds(listChild,childrenIds);
            }
        }
        return childrenIds;
    }

}
