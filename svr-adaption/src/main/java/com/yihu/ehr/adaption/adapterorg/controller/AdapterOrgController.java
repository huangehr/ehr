package com.yihu.ehr.adaption.adapterorg.controller;


import com.yihu.ehr.adaption.adapterorg.service.AdapterOrg;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrgManager;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.parm.FieldCondition;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
@RequestMapping(ApiVersion.CommonVersion)
@Api(protocols = "https", value = "adapterorg", description = "第三方标准管理接口", tags = {"第三方标准"})
public class AdapterOrgController extends ExtendController<MAdapterOrg> {
    @Autowired
    private AdapterOrgManager adapterOrgManager;
    @Autowired
    private OrgAdapterPlanManager orgAdapterPlanManager;

    @RequestMapping(value = "/adapterorgs/page", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Envelop searchAdapterOrg(
            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {

        try {
            return adapterOrgManager.pagesToResult(jsonToObj(parmJson, PageModel.class));
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/adapterorg/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public MAdapterOrg getAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code) {

        try {
            if (StringUtils.isEmpty(code))
                throw errMissCode();
            return getModel(adapterOrgManager.findOne(code));
        } catch (Exception ex) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/adapterorg", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    public boolean addAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "adapterOrgModel", value = "采集机构json模型", defaultValue = "")
            @RequestParam(value = "adapterOrgModel", required = false) String adapterOrgModel) {

        try {
            AdapterOrg adapterOrg = jsonToObj(adapterOrgModel, AdapterOrg.class);
            if (adapterOrgManager.findOne(adapterOrg.getCode()) != null) {
                throw new ApiException(ErrorCode.RepeatAdapterOrg, "该机构已存在采集标准！");
            }
            adapterOrgManager.addAdapterOrg(adapterOrg, apiVersion);
            return true;
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/adapterorg/{code}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    public boolean updateAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) {

        try {
            AdapterOrg adapterOrg = adapterOrgManager.findOne(code);
            if (adapterOrg == null)
                throw errNotFound();
            adapterOrg.setName(name);
            adapterOrg.setDescription(description);
            adapterOrgManager.save(adapterOrg);
            return true;
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/adapterorg/{code}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    public boolean delAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        try {
            if (StringUtils.isEmpty(code))
                throw errMissCode();
            adapterOrgManager.delete(code.split(","), code);
            return true;
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/adapterorg/{org}/isExistAdapterData", method = RequestMethod.GET)
    @ApiOperation(value = "判断采集机构是否存在采集数据")
    public boolean orgIsExistData(
            @ApiParam(name = "org", value = "机构", defaultValue = "")
            @PathVariable(value = "org") String org) {

        try {
            return adapterOrgManager.isExistData(org);
        } catch (Exception ex) {
            throw errSystem();
        }
    }

    @RequestMapping(value = "/adapterorgs/{type}", method = RequestMethod.GET)
    @ApiOperation(value = "根据类型获取所有采集标准")
    public List getAdapterOrgList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @PathVariable(value = "type") String type,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "mode", value = "新增或修改", defaultValue = "")
            @RequestParam(value = "mode") String mode) {

        try {
            if (StringUtils.isEmpty(version))
                throw errMissVersion();
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
            pageModel.addFieldCondition(new FieldCondition("orgCode", "not in", orgList));
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
            return adapterOrgs;
        } catch (Exception ex) {
            throw errSystem();
        }
    }
}
