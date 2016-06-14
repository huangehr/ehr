package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.service.ResourcesCategoryService;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
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
    public boolean deleteResourceCategory(
            @ApiParam(name = "id", value = "资源类别ID", defaultValue = "string")
            @PathVariable(value = "id") String id) throws Exception {
        rsCategoryService.deleteRsCategory(id);
        return true;
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

}
