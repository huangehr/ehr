package com.yihu.ehr.resource.controller;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.service.intf.IResourcesCategoryService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
public class ResourcesCategoryController extends BaseRestController {

    @Autowired
    private IResourcesCategoryService rsCategoryService;

    @RequestMapping(value= ServiceApi.Resources.Categories,method = RequestMethod.POST)
    @ApiOperation("资源类别创建")
    public MRsCategory createRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(name = "resourceCategory") String resourceCategory) throws Exception
    {
        RsCategory rsCategory = toEntity(resourceCategory, RsCategory.class);
        rsCategory.setId(getObjectId(BizObject.ResourceCategory));
        rsCategoryService.createOrUpdRsCategory(rsCategory);

        return convertToModel(rsCategory, MRsCategory.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Categories,method=RequestMethod.PUT)
    @ApiOperation("资源类别更新")
    public MRsCategory updateRsCategory(
            @ApiParam(name="resourceCategory",value="资源分类", defaultValue = "{\"id\":\"string\",\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(name="resourceCategory")String resourceCategory) throws  Exception
    {
        RsCategory rsCategory = toEntity(resourceCategory, RsCategory.class);
        rsCategoryService.createOrUpdRsCategory(rsCategory);

        return convertToModel(rsCategory, MRsCategory.class);
    }

    @RequestMapping(value=ServiceApi.Resources.Category,method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    public boolean deleteResourceCategory(
            @ApiParam(name="id",value="资源类别ID",defaultValue = "string")
            @PathVariable(value="id") String id) throws Exception
    {
        rsCategoryService.deleteRsCategory(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.Categories,method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public Page<MRsCategory> getRsCategories(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(name="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(name="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(name="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(name="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(name="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws  Exception
    {
        Pageable pageable = new PageRequest(reducePage(page),size);
        long total = 0;
        Collection<MRsCategory> rsList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsCategory> resources = rsCategoryService.getRsCategories(sorts,reducePage(page),size);
            total = resources.getTotalElements();
            rsList = convertToModels(resources.getContent(),new ArrayList<>(resources.getNumber()),MRsCategory.class,fields);
        }
        else
        {
            List<RsCategory> resources = rsCategoryService.search(fields,filters,sorts,page,size);
            total = rsCategoryService.getCount(filters);
            rsList = convertToModels(resources,new ArrayList<>(resources.size()),MRsCategory.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        Page<MRsCategory> rsPage = new PageImpl<MRsCategory>((List<MRsCategory>)rsList,pageable,total);

        return rsPage;
    }

}
