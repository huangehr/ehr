package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.adapter.service.AdapterOrgClient;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapterOrg")
@RestController
@Api(protocols = "https", value = "adapter", description = "机构", tags = {"机构"})
public class AdapterOrgController extends BaseController {

    @Autowired
    private AdapterOrgClient adapterOrgClient;

    @Autowired
    private ConventionalDictEntryClient dictEntryClient;

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/orgs", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Envelop searchAdapterOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        try {
            ResponseEntity<Collection<MAdapterOrg>> responseEntity = adapterOrgClient.searchAdapterOrg(fields, filters, sorts, size, page);
            List<MAdapterOrg> mAdapterOrgs = (List<MAdapterOrg>) responseEntity.getBody();

            List<AdapterOrgModel> adapterOrgModels = new ArrayList<>();

            for (MAdapterOrg mAdapterOrg : mAdapterOrgs) {
                AdapterOrgModel adapterOrgModel = convertToModel(mAdapterOrg, AdapterOrgModel.class);
                String type = adapterOrgModel.getType();
                if (StringUtils.isNotEmpty(type)) {
                    MConventionalDict dict = dictEntryClient.getAdapterType(type);
                    adapterOrgModel.setTypeValue(dict == null ? "" : dict.getValue());
                }
                String orgCode = adapterOrgModel.getOrg();
                if (StringUtils.isNotEmpty(orgCode)) {
                    MOrganization mOrganization = organizationClient.getOrg(orgCode);
                    adapterOrgModel.setOrgValue(mAdapterOrg == null ? "" : mOrganization.getFullName());
                }
                String parentId = adapterOrgModel.getParent();
                if (StringUtils.isNotEmpty(parentId)) {
                    MAdapterOrg adapterOrg = adapterOrgClient.getAdapterOrg(parentId);
                    adapterOrgModel.setParentValue(adapterOrg == null ? "" : adapterOrg.getName());
                }
                adapterOrgModels.add(adapterOrgModel);
            }

            return getResult(adapterOrgModels, getTotalCount(responseEntity), page, size);
        } catch (Exception ex) {
            return failedSystem();
        }
    }


    @RequestMapping(value = "/org/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配采集标准")
    public Envelop getAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code) throws Exception {

        MAdapterOrg mAdapterOrg = adapterOrgClient.getAdapterOrg(code);

        AdapterOrgDetailModel adapterOrgModel = convertAdapterOrgDetailModel(mAdapterOrg);
        if (adapterOrgModel == null) {
            return failed("适配机构信息获取失败!");
        }

        return success(adapterOrgModel);
    }


    @RequestMapping(value = "/org", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    public Envelop addAdapterOrg(
            @ApiParam(name = "adapterOrg", value = "采集机构模型", defaultValue = "")
            @RequestParam(value = "adapterOrg", required = false) String adapterOrg) {
        MAdapterOrg mAdapterOrg = null;
        try {
            AdapterOrgDetailModel detailModel = objectMapper.readValue(adapterOrg, AdapterOrgDetailModel.class);

            String errorMsg = "";
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "名称不能为空!";
            }

            if (StringUtils.isEmpty(detailModel.getOrg())) {
                errorMsg += "机构代码不能为空!";
            }

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            if (adapterOrgClient.isExistAdapterOrg(detailModel.getCode())) {
                return failed("该机构已存在采集标准！");
            }

            mAdapterOrg = convertToModel(detailModel, MAdapterOrg.class);
            mAdapterOrg = adapterOrgClient.addAdapterOrg(objectMapper.writeValueAsString(mAdapterOrg));
            if (mAdapterOrg == null) {
                return failed("新增失败!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
        return success(convertAdapterOrgDetailModel(mAdapterOrg));
    }


    @RequestMapping(value = "/org/{code}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    public Envelop updateAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        String errorMsg = "";

        if (StringUtils.isEmpty(code)) {
            errorMsg += "代码不能为空!";
        }
        if (StringUtils.isEmpty(name)) {
            errorMsg += "名称不能为空!";
        }

        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }

        MAdapterOrg mAdapterOrg = adapterOrgClient.updateAdapterOrg(code, name, description);
        if (mAdapterOrg == null) {
            return failed("修改失败!");
        }
        return success(convertAdapterOrgDetailModel(mAdapterOrg));
    }


    @RequestMapping(value = "/orgs", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    public Envelop delAdapterOrg(
            @ApiParam(name = "codes", value = "代码", defaultValue = "")
            @RequestParam(value = "codes") String codes) {
        try {
            codes = trimEnd(codes, ",");
            if (StringUtils.isEmpty(codes)) {
                return failed("请选择需要删除的内容!");
            }
            boolean result = adapterOrgClient.delAdapterOrg(codes);
            if (!result) {
                return failed("删除失败!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
        return success(null);
    }


    @RequestMapping(value = "/{org}/isExistAdapterData", method = RequestMethod.GET)
    @ApiOperation(value = "判断采集机构是否存在采集数据")
    public boolean orgIsExistData(
            @ApiParam(name = "org_code", value = "机构", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception {

        return adapterOrgClient.orgIsExistData(orgCode);
    }

    public AdapterOrgDetailModel convertAdapterOrgDetailModel(MAdapterOrg mAdapterOrg) {
        AdapterOrgDetailModel detailModel = convertToModel(mAdapterOrg, AdapterOrgDetailModel.class);
        String type = detailModel.getType();
        if (StringUtils.isNotEmpty(type)) {
            MConventionalDict dict = dictEntryClient.getAdapterType(type);
            detailModel.setTypeValue(dict == null ? "" : dict.getValue());
        }
        String orgCode = detailModel.getOrg();
        if (StringUtils.isNotEmpty(orgCode)) {
            MOrganization mOrganization = organizationClient.getOrg(orgCode);
            detailModel.setOrgValue(mAdapterOrg == null ? "" : mOrganization.getFullName());
        }
        String parentId = detailModel.getParent();
        if (StringUtils.isNotEmpty(parentId)) {
            MAdapterOrg adapterOrg = adapterOrgClient.getAdapterOrg(parentId);
            detailModel.setParentValue(adapterOrg == null ? "" : adapterOrg.getName());
        }

        return detailModel;
    }
}
