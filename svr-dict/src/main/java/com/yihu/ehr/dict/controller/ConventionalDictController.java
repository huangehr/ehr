package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictEntryService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 惯用字典接口，用于快速提取常用的字典项。
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Conventional-Dictionaries", description = "获取常用字典项", tags = {"惯用字典"})
public class ConventionalDictController extends BaseRestController {

    @Autowired
    private SystemDictEntryService dictEntryService;

    MConventionalDict getDictModel(Object dictEntry) {
        return convertToModel(dictEntry, MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/app_catalog", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用类别字典项", response = MConventionalDict.class)
    public MConventionalDict getAppCatalog(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry appCatalog = dictEntryService.getDictEntry(1, code);

        return getDictModel(appCatalog);
    }

    @RequestMapping(value = "/dictionaries/app_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用状态字典项", response = MConventionalDict.class)
    public MConventionalDict getAppStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry appStatus = dictEntryService.getDictEntry(2, code);

        return getDictModel(appStatus);
    }

    @RequestMapping(value = "/dictionaries/gender", method = RequestMethod.GET)
    @ApiOperation(value = "获取性别字典项", response = MConventionalDict.class)
    public MConventionalDict getGender(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry gender = dictEntryService.getDictEntry(3, code);

        return getDictModel(gender);
    }

    @RequestMapping(value = "/dictionaries/martial_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取婚姻状态字典项", response = MConventionalDict.class)
    public MConventionalDict getMartialStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry martialStatus = dictEntryService.getDictEntry(4, code);

        return getDictModel(martialStatus);
    }

    @RequestMapping(value = "/dictionaries/nation", method = RequestMethod.GET)
    @ApiOperation(value = "获取国家字典项", response = MConventionalDict.class)
    public MConventionalDict getNation(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry nation = dictEntryService.getDictEntry(5, code);

        return getDictModel(nation);
    }

    @RequestMapping(value = "/dictionaries/residence_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取人口居住类型字典项", response = MConventionalDict.class)
    public MConventionalDict getResidenceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry residenceType = dictEntryService.getDictEntry(6, code);

        return getDictModel(residenceType);
    }

    @RequestMapping(value = "/dictionaries/org_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取组织机构类别字典项", response = MConventionalDict.class)
    public MConventionalDict getOrgType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry orgType = dictEntryService.getDictEntry(7, code);

        return getDictModel(orgType);
    }

    @RequestMapping(value = "/dictionaries/settled_way", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构入驻方式字典项", response = MConventionalDict.class)
    public MConventionalDict getSettledWay(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry settledWay = dictEntryService.getDictEntry(8, code);

        return getDictModel(settledWay);
    }

    @RequestMapping(value = "/dictionaries/card_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡状态字典项", response = MConventionalDict.class)
    public MConventionalDict getCardStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry cardStatus = dictEntryService.getDictEntry(9, code);

        return getDictModel(cardStatus);
    }

    @RequestMapping(value = "/dictionaries/card_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡类别字典项", response = MConventionalDict.class)
    public MConventionalDict getCardType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry cardType = dictEntryService.getDictEntry(10, code);

        return getDictModel(cardType);
    }

    @RequestMapping(value = "/dictionaries/request_state", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员请求消息状态字典项", response = MConventionalDict.class)
    public MConventionalDict getRequestState(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry requestState = dictEntryService.getDictEntry(11, code);

        return getDictModel(requestState);
    }

    @RequestMapping(value = "/dictionaries/key_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取密钥类型字典项", response = MConventionalDict.class)
    public MConventionalDict getKeyType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry keyType = dictEntryService.getDictEntry(12, code);

        return getDictModel(keyType);
    }

    @RequestMapping(value = "/dictionaries/medical_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取医疗角色字典项", response = MConventionalDict.class)
    public MConventionalDict getMedicalRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry medicalRole = dictEntryService.getDictEntry(13, code);

        return getDictModel(medicalRole);
    }

    @RequestMapping(value = "/dictionaries/user_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户角色字典项", response = MConventionalDict.class)
    public MConventionalDict getUserRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry userRole = dictEntryService.getDictEntry(14, code);

        return getDictModel(userRole);
    }

    @RequestMapping(value = "/dictionaries/user_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class)
    public MConventionalDict getUserType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry userType = dictEntryService.getDictEntry(15, code);

        return getDictModel(userType);
    }

    @RequestMapping(value = "/dictionaries/3rd_app", method = RequestMethod.GET)
    @ApiOperation(value = "获取连接的第三方应用字典项", response = MConventionalDict.class)
    public MConventionalDict getLoginAddress(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry loginAddress = dictEntryService.getDictEntry(20, code);

        return getDictModel(loginAddress);
    }

    @RequestMapping(value = "/dictionaries/yes_no", method = RequestMethod.GET)
    @ApiOperation(value = "获取是否字典项", response = MConventionalDict.class)
    public MConventionalDict getYesNo(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {
        String resultCode = code ? "true" : "false";
        SystemDictEntry yesNo = dictEntryService.getDictEntry(18, resultCode);

        return getDictModel(yesNo);
    }

    @RequestMapping(value = "/dictionaries/adaption_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配类型字典项", response = MConventionalDict.class)
    public MConventionalDict getAdapterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry adapterType = dictEntryService.getDictEntry(21, code);

        return getDictModel(adapterType);
    }

    @RequestMapping(value = "/dictionaries/std_source_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源字典项", response = MConventionalDict.class)
    public MConventionalDict getStdSourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry stdSourceType = dictEntryService.getDictEntry(22, code);

        return getDictModel(stdSourceType);
    }


    @RequestMapping(value = "/dictionaries/std_source_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源类型字典项", response = MConventionalDict.class)
    public Collection<MConventionalDict> getStdSourceTypeList(
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") String[] codes) {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(22, codes);

        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/user_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class)
    public Collection<MConventionalDict> getUserTypeList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(15, null);

        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取标签字典项", response = MConventionalDict.class)
    public Collection<MConventionalDict> getTagsList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(17, null);

        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/indicator_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标类型", response = MConventionalDict.class)
    public MConventionalDict getIndicatorType(
        @ApiParam(name = "code", value = "字典代码", defaultValue = "")
        @RequestParam(value = "code") String code) {
            SystemDictEntry indicatorType = dictEntryService.getDictEntry(23, code);
            return getDictModel(indicatorType);
    }

    @RequestMapping(value = "/dictionaries/drug_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取药品类型", response = MConventionalDict.class)
    public MConventionalDict getDrugType(
        @ApiParam(name = "code", value = "字典代码", defaultValue = "")
        @RequestParam(value = "code") String code) {
            SystemDictEntry drugType = dictEntryService.getDictEntry(24, code);

            return getDictModel(drugType);
    }

    @RequestMapping(value = "/dictionaries/drug_flag", method = RequestMethod.GET)
    @ApiOperation(value = "获取药品处方标识", response = MConventionalDict.class)
    public MConventionalDict getDrugFlag(
        @ApiParam(name = "code", value = "字典代码", defaultValue = "")
        @RequestParam(value = "code") String code) {
            SystemDictEntry drugFlag = dictEntryService.getDictEntry(25, code);

        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/user_source", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户来源字典项", response = MConventionalDict.class)
    public MConventionalDict getUserSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(26, code);

        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/family_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭关系字典项", response = MConventionalDict.class)
    public MConventionalDict getFamilyRelationship(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(16, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/resource_adapt_scheme", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源适配方案类别字典项", response = MConventionalDict.class)
    public MConventionalDict getResourceAdaptScheme(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(39, code);
        return getDictModel(drugFlag);
    }


    @RequestMapping(value = "/dictionaries/mete_data_field_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元字段类型字典项", response = MConventionalDict.class)
    public MConventionalDict getMeteDataFieldType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(40, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/resource_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源分类字典项", response = MConventionalDict.class)
    public MConventionalDict getResourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(42, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/business_domain", method = RequestMethod.GET)
    @ApiOperation(value = "获取业务领域字典项", response = MConventionalDict.class)
    public MConventionalDict getBusinessDomain(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(41, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/resource_access_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源访问方式字典项", response = MConventionalDict.class)
    public MConventionalDict getResourceAccessType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(43, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/logical_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "获取逻辑关系字典项", response = MConventionalDict.class)
    public MConventionalDict getLogicalRelationship(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(45, code);
        return getDictModel(drugFlag);
    }


}
