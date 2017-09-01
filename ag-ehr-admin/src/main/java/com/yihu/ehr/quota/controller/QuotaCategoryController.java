package com.yihu.ehr.quota.controller;

import com.yihu.ehr.agModel.tj.QuotaCategoryModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.tj.MQuotaCategory;
import com.yihu.ehr.quota.service.QuotaCategoryClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxw on 2017/8/31.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "QuotaCategory", description = "指标分类管理", tags = {"指标分类-管理"})
public class QuotaCategoryController extends BaseController {
    @Autowired
    private QuotaCategoryClient quotaCategoryClient;

    @RequestMapping(value = "/quotaCategory/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标分类列表")
    public Envelop getQuotaCategoryList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        ListResult listResult = quotaCategoryClient.getQuotaCategoryList(fields, filters, sorts, size, page);

        List<QuotaCategoryModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                QuotaCategoryModel quotaCategoryModel = objectMapper.convertValue(map,QuotaCategoryModel.class);
                if (quotaCategoryModel.getParentId() != 0) {
                    MQuotaCategory parent = quotaCategoryClient.searchQuotaCategoryDetail(quotaCategoryModel.getParentId());
                    quotaCategoryModel.setParentName(parent.getName());
                }
                mainModelList.add(quotaCategoryModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @ApiOperation(value = "指标分类列表，不分页")
    @RequestMapping(value = "/quotaCategory/list", method = RequestMethod.GET)
    public Envelop getAllQuotaCategory() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(quotaCategoryClient.getAllQuotaCategory());
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "根据父ID获取子指标分类列表")
    @RequestMapping(value = "/quotaCategory/childs", method = RequestMethod.GET)
    public Envelop searchChildQuotaCategory(
            @ApiParam(name = "parentId", value = "父ID" )
            @RequestParam(value = "parentId") Integer parentId) {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(quotaCategoryClient.searchChildQuotaCategory(parentId));
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/quotaCategory/detailById" , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    public Envelop detail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id){
        try {
            String errorMsg = "";

            if (id == null) {
                errorMsg += "id不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MQuotaCategory mQuotaCategory = quotaCategoryClient.searchQuotaCategoryDetail(id);
            if (mQuotaCategory == null) {
                return failed("获取详情失败!");
            }
            if (mQuotaCategory.getParentId() != 0) {
                MQuotaCategory parent = quotaCategoryClient.searchQuotaCategoryDetail(mQuotaCategory.getParentId());
                mQuotaCategory.setParentName(parent.getName());
            }
            return success(mQuotaCategory);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/quotaCategory/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标分类")
    public boolean deleteQuotaCategory(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id) {
        try {
            boolean succ = quotaCategoryClient.deleteQuotaCategory(id);
            return succ;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/quotaCategory/checkName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查名称是否唯一")
    public Envelop checkName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name", required = true) String name){
        try {
            Envelop envelop = new Envelop();
            if (StringUtils.isEmpty(name)) {
                envelop.setErrorMsg("名称不能为空！");
            }
            int num = quotaCategoryClient.getCountByName(name);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已经存在此名称!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/quotaCategory/checkCode" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查编码是否唯一")
    public Envelop checkCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code", required = true) String code){
        try {
            Envelop envelop = new Envelop();
            if (StringUtils.isEmpty(code)) {
                envelop.setErrorMsg("编码不能为空！");
            }
            int num = quotaCategoryClient.getCountByCode(code);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已经存在此编码!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/quotaCategory/add" , method = RequestMethod.POST)
    @ApiOperation(value = "新增指标分类")
    public Envelop create(
            @ApiParam(name = "jsonData", value = " 指标分类信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData){
        try {
            String errorMsg = "";
            QuotaCategoryModel quotaCategoryModel = objectMapper.readValue(jsonData, QuotaCategoryModel.class);

            if (StringUtils.isEmpty(quotaCategoryModel.getCode())) {
                errorMsg+="编码不能为空！";
            }
            if (StringUtils.isEmpty(quotaCategoryModel.getName())) {
                errorMsg+="名称不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            String json = objectMapper.writeValueAsString(quotaCategoryModel);
            MQuotaCategory mQuotaCategory = quotaCategoryClient.saveQuotaCategory(json);
            if (mQuotaCategory == null) {
                return failed("保存失败!");
            }
            return success(mQuotaCategory);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/quotaCategory/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改指标分类")
    public Envelop resetInfo(
            @ApiParam(name = "jsonData", value = " 指标分类信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData){
        try {
            String errorMsg = "";
            QuotaCategoryModel quotaCategoryModel = objectMapper.readValue(jsonData, QuotaCategoryModel.class);
            if (null == quotaCategoryModel) {
                errorMsg += "内容出错！";
            }
            if (StringUtils.isEmpty(quotaCategoryModel.getName())) {
                errorMsg += "名称不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            String json = objectMapper.writeValueAsString(quotaCategoryModel);
            MQuotaCategory mQuotaCategory = quotaCategoryClient.updateQuotaCategory(json);
            if (mQuotaCategory == null) {
                return failed("修改指标分类失败!");
            }
            return success(mQuotaCategory);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "获取指标分类子类列表")
    @RequestMapping(value = "/quotaCategory/getQuotaCategoryChild", method = RequestMethod.GET)
    public Envelop getQuotaCategoryChild() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(quotaCategoryClient.getQuotaCategoryChild());
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
