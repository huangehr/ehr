package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsAdapterDictionary;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.resource.client.ResourcesCategoryClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceCategory", description = "资源分类服务接口")
public class ResourcesCategoryController extends BaseController {

    @Autowired
    private ResourcesCategoryClient resourcesCategoryClient;

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.POST)
    @ApiOperation("资源类别创建")
    public Envelop createRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(value = "resourceCategory") String resourceCategory) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsCategory rsCategory = resourcesCategoryClient.createRsCategory(resourceCategory);
            envelop.setObj(rsCategory);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.PUT)
    @ApiOperation("资源类别更新")
    public Envelop updateRsCategory(
            @ApiParam(name = "resourceCategory", value = "资源分类", defaultValue = "{\"id\":\"string\",\"name\":\"string\",\"pid\":\"string\",\"description\":\"string\"}")
            @RequestParam(value = "resourceCategory") String resourceCategory) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsCategory rsCategory = resourcesCategoryClient.updateRsCategory(resourceCategory);
            envelop.setObj(rsCategory);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Category, method = RequestMethod.DELETE)
    @ApiOperation("删除资源类别")
    public Envelop deleteResourceCategory(
            @ApiParam(name = "id", value = "资源类别ID", defaultValue = "string")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesCategoryClient.deleteResourceCategory(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
    @RequestMapping(value = ServiceApi.Resources.Category,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源类别")
    public Envelop getRsCategoryById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsCategory rsCategory = resourcesCategoryClient.getRsCategoryById(id);
            envelop.setObj(rsCategory);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Categories, method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public Envelop getRsCategories(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try {
            ResponseEntity<List<MRsCategory>> responseEntity = resourcesCategoryClient.getRsCategories(fields,filters,sorts,page,size);
            List<MRsCategory> rsCategories = responseEntity.getBody();
            Envelop envelop = getResult(rsCategories, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Resources.NoPageCategories,method = RequestMethod.GET)
    @ApiOperation("获取资源类别")
    public Envelop getAllCategories(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsCategory> resources = resourcesCategoryClient.getAllCategories(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


}
