package com.yihu.ehr.adaption.adapterorg.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrg;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrgManager;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.parm.FieldCondition;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterorg")
@Api(protocols = "https", value = "adapterorg", description = "第三方标准管理接口", tags = {"第三方标准"})
public class AdapterOrgController extends BaseRestController {
    @Autowired
    private AdapterOrgManager adapterOrgManager;
    @Autowired
    private OrgAdapterPlanManager orgAdapterPlanManager;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Result searchAdapterOrg(
            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {

        Result result = new Result();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
            result = adapterOrgManager.pagesToResult(pageModel);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Result getAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Result result = new Result();
        try {
            AdapterOrg adapterOrg = adapterOrgManager.findOne(code);
            result.setObj(adapterOrg);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    public boolean addAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "adapterOrgModel", value = "采集机构json模型", defaultValue = "")
            @RequestParam(value = "adapterOrgModel", required = false) String adapterOrgModel) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AdapterOrg adapterOrg = objectMapper.readValue(adapterOrgModel, AdapterOrg.class);
            if (adapterOrgManager.findOne(adapterOrg.getCode()) != null) {
                failed(ErrorCode.ExistOrgForCreate, "该机构已存在采集标准！");
            }
            adapterOrgManager.addAdapterOrg(adapterOrg, apiVersion);
            return true;
        } catch (ApiException e) {
            failed(e.getErrorCode(), e.getErrMsg());
            return false;
        } catch (Exception e) {
//            failed(ErrorCode.SaveFailed, "保存失败！", e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    public boolean updateAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description) {
        try {
            AdapterOrg adapterOrg = adapterOrgManager.findOne(code);
            adapterOrg.setName(name);
            adapterOrg.setDescription(description);
            adapterOrgManager.save(adapterOrg);
            return true;
        } catch (Exception e) {
//            failed(ErrorCode.SaveFailed, "更新失败！", e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    public boolean delAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        try {
            adapterOrgManager.delete(code.split(","), code);
//            adapterOrgManager.deleteAdapterOrg(code.split(","));
        } catch (Exception e) {
//            failed(ErrorCode.SaveFailed, "删除失败！", e.getMessage());
            return false;
        }
        return true;
    }

    //获取初始标准列表  重复
//    @RequestMapping(value = "/list" , method = RequestMethod.GET)
//    @ApiOperation(value = "获取初始标准列表")
    public Result getAdapterOrgList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "")
            @RequestParam(value = "rows") int rows) {
        //根据类型获取所有采集标准
        Result result = new Result();
        try {
            PageModel pageModel = new PageModel(page, rows);
            FieldCondition fieldCondition = new FieldCondition("type", "in", "1");
            //厂商，初始标准只能是厂商
            if ("2".equals(type)) {
                //医院，初始标准没有限制
                fieldCondition.addVal("1", "3");
            } else if ("3".equals(type)) {
                //区域,初始标准只能选择厂商或区域
                fieldCondition.addVal("1");
            }
            pageModel.addFieldCondition(fieldCondition);
            List<AdapterOrg> ls = adapterOrgManager.pages(pageModel);
            Integer totalCount = adapterOrgManager.totalCountForPage(pageModel);
            List<String> adapterOrgs = new ArrayList<>();
            for (AdapterOrg adapterOrg : ls) {
                adapterOrgs.add(adapterOrg.getCode() + ',' + adapterOrg.getName());
            }
            result = getResult(adapterOrgs, totalCount, pageModel.getPage(), pageModel.getRows());
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping(value = "/orgIsExistData", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否存在")
    public boolean orgIsExistData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "org", value = "机构", defaultValue = "")
            @RequestParam(value = "org") String org) {
        try {
            return adapterOrgManager.isExistData(org);
        } catch (Exception ex) {
            return false;
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案机构列表")
    public String getAdapterOrgList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @PathVariable(value = "type") String type,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "mode", value = "新增或修改", defaultValue = "")
            @PathVariable(value = "mode") String mode) {
        //根据类型获取所有采集标准
        Result result = new Result();
        try {
            List<AdapterOrg> adapterOrgList = null;
            List<OrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.findList("", version);
            List<String> orgList = new ArrayList<>();
            if (!mode.equals("modify")) {
                for (OrgAdapterPlan xOrgAdapterPlan : orgAdapterPlans) {
                    orgList.add(xOrgAdapterPlan.getOrg());
                }
            }

            PageModel pageModel = new PageModel();
            pageModel.addFieldCondition(new FieldCondition("type", "=", type));
            pageModel.addFieldCondition(new FieldCondition("orgCode", "in", orgList));
            adapterOrgList = adapterOrgManager.pages(pageModel);

            List<Map> adapterOrgs = new ArrayList<>();
            if (!adapterOrgList.isEmpty()) {
                for (AdapterOrg adapterOrg : adapterOrgList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", adapterOrg.getCode());
                    map.put("value", adapterOrg.getName());
                    adapterOrgs.add(map);
                }
            }
            result.setSuccessFlg(true);
            result.setDetailModelList(adapterOrgs);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }


}
