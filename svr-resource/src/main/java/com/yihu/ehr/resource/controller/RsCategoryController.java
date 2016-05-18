package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.service.RsCategoryService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/categories")
@Api(value = "categories", description = "资源类别服务管理接口")
public class RsCategoryController extends BaseRestController {
    @Autowired
    private RsCategoryService rsCategoryService;

    @RequestMapping(value = "/searchRsCategories", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源类别列表", notes = "根据查询条件获取资源类别列表")
    public List<MRsCategory> searchRsCategories(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsCategory> categories = rsCategoryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rsCategoryService.getCount(filters), page, size);

        return (List<MRsCategory>) convertToModels(categories, new ArrayList<MRsCategory>(categories.size()), MRsCategory.class, fields);
    }



    @RequestMapping(value = "/createRsCategory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建资源类别", notes = "创建资源类别")
    public MRsCategory createRsCategory(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsCategory category = toEntity(jsonData, RsCategory.class);
        rsCategoryService.save(category);
        return convertToModel(category, MRsCategory.class, null);

    }

    @RequestMapping(value = "/updateRsCategory", method = RequestMethod.PUT)
    @ApiOperation(value = "修改资源类别", notes = "修改资源类别")
    public MRsCategory updateRsDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsCategory category = toEntity(jsonData, RsCategory.class);
        rsCategoryService.save(category);
        return convertToModel(category, MRsCategory.class, null);
    }

    @RequestMapping(value = "/deleteRsCategory{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源类别", notes = "删除资源类别")
    public boolean deleteRsCategory(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        rsCategoryService.delete(id);
        return true;
    }
}