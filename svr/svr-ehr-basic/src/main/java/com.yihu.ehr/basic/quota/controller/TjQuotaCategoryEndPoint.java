package com.yihu.ehr.basic.quota.controller;

import com.yihu.ehr.basic.quota.service.TjQuotaCategoryService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.TjQuotaCategory;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.tj.MQuotaCategory;
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
 * Created by wxw on 2017/8/31.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "QuotaCategory", description = "指标分类管理", tags = {"指标分类-管理"})
public class TjQuotaCategoryEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private TjQuotaCategoryService quotaCategoryService;

    @RequestMapping(value = "/quotaCategory/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标分类列表")
    public ListResult getQuotaCategoryList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<TjQuotaCategory> quotaCategoryList = quotaCategoryService.search(fields, filters, sorts, page, size);
        if(quotaCategoryList != null){
            listResult.setDetailModelList(quotaCategoryList);
            listResult.setTotalCount((int)quotaCategoryService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = "/quotaCategory/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取指标分类列表")
    public List<MQuotaCategory> getAllQuotaCategory() throws Exception {
        List<TjQuotaCategory> quotaCategories = quotaCategoryService.getAllQuotaCategory();
        return (List<MQuotaCategory>) convertToModels(quotaCategories, new ArrayList<MQuotaCategory>(quotaCategories.size()), MQuotaCategory.class, null);
    }

    @ApiOperation(value = "根据父ID获取子指标分类列表")
    @RequestMapping(value = "/quotaCategory/childs", method = RequestMethod.GET)
    public List<MQuotaCategory> searchChildQuotaCategory(
            @RequestParam(value = "parentId", required = true) Integer parentId) throws Exception {
        List<TjQuotaCategory> quotaCategories = quotaCategoryService.searchByParentId(parentId);
        return (List<MQuotaCategory>) convertToModels(quotaCategories, new ArrayList<MQuotaCategory>(quotaCategories.size()), MQuotaCategory.class, null);
    }

    @RequestMapping(value = "/quotaCategory/detailById" , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    public MQuotaCategory searchQuotaCategoryDetail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id) throws Exception {
        TjQuotaCategory quotaCategory = quotaCategoryService.getById(id);
        MQuotaCategory mQuotaCategory = convertToModel(quotaCategory, MQuotaCategory.class);
        return mQuotaCategory;
    }

    @RequestMapping(value = "/quotaCategory/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标分类")
    public boolean deleteQquotaCategory(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id
    ) throws Exception {
        quotaCategoryService.deleteQuotaCategory(id);
        return true;
    }

    @RequestMapping(value = "/quotaCategory/checkName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查名称是否唯一")
    public int getCountByName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name", required = true) String name) throws Exception {
        return quotaCategoryService.getCountByName(name);
    }

    @RequestMapping(value = "/quotaCategory/checkCode" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查编码是否唯一")
    public int getCountByCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code", required = true) String code) throws Exception {
        return quotaCategoryService.getCountByCode(code);
    }

    @RequestMapping(value = "/quotaCategory/add" , method = RequestMethod.POST)
    @ApiOperation(value = "新增指标分类")
    public MQuotaCategory saveQuotaCategory(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData) {
        try {
            TjQuotaCategory quotaCategory = toEntity(jsonData, TjQuotaCategory.class);
            quotaCategory = quotaCategoryService.saveQuotaCategory(quotaCategory);
            return convertToModel(quotaCategory, MQuotaCategory.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/quotaCategory/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改指标分类")
    public MQuotaCategory updateQuotaCategory(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData) {
        try {
            TjQuotaCategory quotaCategory = toEntity(jsonData, TjQuotaCategory.class);
            quotaCategory = quotaCategoryService.updateQuotaCategory(quotaCategory);
            return convertToModel(quotaCategory, MQuotaCategory.class);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/quota/getQuotaCategoryOfChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类医疗服务子类目列表")
    public List<MQuotaCategory> getQuotaCategoryOfChild() {
        List<TjQuotaCategory> quotaCategories = quotaCategoryService.getQuotaCategoryOfChild();
        return (List<MQuotaCategory>) convertToModels(quotaCategories, new ArrayList<MQuotaCategory>(quotaCategories.size()), MQuotaCategory.class, null);
    }

    @RequestMapping(value = "/quota/getQuotaCategoryChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类子类列表")
    public List<MQuotaCategory> getQuotaCategoryChild() {
        List<TjQuotaCategory> quotaCategories = quotaCategoryService.getQuotaCategoryChild();
        return (List<MQuotaCategory>) convertToModels(quotaCategories, new ArrayList<MQuotaCategory>(quotaCategories.size()), MQuotaCategory.class, null);
    }
}
