package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictEntryService;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@Api(value = "Conventional-Dictionaries", description = "获取常用字典项", tags = {"系统字典-惯用字典"})
public class ConventionalDictEndPoint extends EnvelopRestEndPoint {

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

    @RequestMapping(value = "/dictionaries/record_data_sources", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案数据来源", response = MConventionalDict.class)
    public Collection<MConventionalDict> getRecordDataSourceList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(35, null);

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
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(29, code);
        return getDictModel(drugFlag);
    }


    @RequestMapping(value = "/dictionaries/mete_data_field_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元字段类型字典项", response = MConventionalDict.class)
    public MConventionalDict getMeteDataFieldType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(30, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/business_domain", method = RequestMethod.GET)
    @ApiOperation(value = "获取业务领域字典项", response = MConventionalDict.class)
    public MConventionalDict getBusinessDomain(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(31, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/resource_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源分类字典项", response = MConventionalDict.class)
    public MConventionalDict getResourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(32, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/resource_access_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源访问方式字典项", response = MConventionalDict.class)
    public MConventionalDict getResourceAccessType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(33, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/logical_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "获取逻辑关系", response = MConventionalDict.class)
    public MConventionalDict getLogicalRelationship(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(34, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/logical_relationship_list", method = RequestMethod.GET)
    @ApiOperation(value = "获取逻辑关系", response = MConventionalDict.class)
    public Collection<MConventionalDict> getLogicalRelationshipList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(34, null);

        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/record_data_source", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案数据来源，", response = MConventionalDict.class)
    public MConventionalDict getRecordDataSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(35, code);
        return getDictModel(drugFlag);
    }


    @RequestMapping(value = "/dictionaries/archives_audit_status", method = RequestMethod.GET)
    @ApiOperation(value = "档案关联审批状态", response = MConventionalDict.class)
    public MConventionalDict getArchivesAuditStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(36, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/archives_audit_status_list", method = RequestMethod.GET)
    @ApiOperation(value = "档案关联审批状态", response = MConventionalDict.class)
    public Collection<MConventionalDict> getArchivesAuditStatusList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(36, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/archives_manage_status", method = RequestMethod.GET)
    @ApiOperation(value = "档案管理状态", response = MConventionalDict.class)
    public MConventionalDict getArchivesManageStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(37, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/archives_manage_status_list", method = RequestMethod.GET)
    @ApiOperation(value = "档案管理状态", response = MConventionalDict.class)
    public Collection<MConventionalDict> getArchivesManageStatusList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(37, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/application_source", method = RequestMethod.GET)
    @ApiOperation(value = "应用来源", response = MConventionalDict.class)
    public MConventionalDict getApplicationSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(38, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/application_source_list", method = RequestMethod.GET)
    @ApiOperation(value = "应用来源", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApplicationSourceList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(38, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/application_menu_type", method = RequestMethod.GET)
    @ApiOperation(value = "应用菜单类型", response = MConventionalDict.class)
    public MConventionalDict getApplicationMenuType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(39, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/application_menu_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "应用菜单类型", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApplicationMenuTypeList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(39, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }


    @RequestMapping(value = "/dictionaries/open_level", method = RequestMethod.GET)
    @ApiOperation(value = "开放等级", response = MConventionalDict.class)
    public MConventionalDict getOpenLevel(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(40, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/open_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "开放等级", response = MConventionalDict.class)
    public Collection<MConventionalDict> getOpenLevelList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(40, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/audit_level", method = RequestMethod.GET)
    @ApiOperation(value = "审计等级", response = MConventionalDict.class)
    public MConventionalDict getAuditLevel(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(41, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/audit_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "审计等级", response = MConventionalDict.class)
    public Collection<MConventionalDict> getAuditLevelList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(41, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/role_type", method = RequestMethod.GET)
    @ApiOperation(value = "角色组分类", response = MConventionalDict.class)
    public MConventionalDict getRoleType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(42, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/role_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "角色组分类", response = MConventionalDict.class)
    public Collection<MConventionalDict> getRoleTypeList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(42, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }


    @RequestMapping(value = "/dictionaries/valid_identification", method = RequestMethod.GET)
    @ApiOperation(value = "有效标识", response = MConventionalDict.class)
    public MConventionalDict getValidIdentification(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(43, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/valid_identification_list", method = RequestMethod.GET)
    @ApiOperation(value = "有效标识", response = MConventionalDict.class)
    public Collection<MConventionalDict> getValidIdentificationList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(43, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/api_protocol", method = RequestMethod.GET)
    @ApiOperation(value = "API协议", response = MConventionalDict.class)
    public MConventionalDict getApiProtocol(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(44, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/api_protocol_list", method = RequestMethod.GET)
    @ApiOperation(value = "API协议", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApiProtocolList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(44, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/api_method", method = RequestMethod.GET)
    @ApiOperation(value = "API方法", response = MConventionalDict.class)
    public MConventionalDict getApiMethod(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(45, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/api_method_list", method = RequestMethod.GET)
    @ApiOperation(value = "API方法", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApiMethodList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(45, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/api_type", method = RequestMethod.GET)
    @ApiOperation(value = "API类型", response = MConventionalDict.class)
    public MConventionalDict getApiType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(46, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/api_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API类型", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApiTypeList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(46, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_type", method = RequestMethod.GET)
    @ApiOperation(value = "API参数类型", response = MConventionalDict.class)
    public MConventionalDict getApiParameterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(47, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数类型", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApiParameterTypeList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(47, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_type", method = RequestMethod.GET)
    @ApiOperation(value = "API参数数据类型", response = MConventionalDict.class)
    public MConventionalDict getApiParameterDataType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(48, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数数据类型", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApiParameterDataTypeList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(48, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_required", method = RequestMethod.GET)
    @ApiOperation(value = "API参数必输标识", response = MConventionalDict.class)
    public MConventionalDict getApiParameterDataRequired(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry drugFlag = dictEntryService.getDictEntry(49, code);
        return getDictModel(drugFlag);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_required_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数必输标识", response = MConventionalDict.class)
    public Collection<MConventionalDict> getApiParameterDataRequiredList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(49, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/cda_type_browser_list", method = RequestMethod.GET)
    @ApiOperation(value = "浏览器用CDA类别清单", response = MConventionalDict.class)
    public Collection<MConventionalDict> getCdaTypeForBrowserList() {
        List<SystemDictEntry> list = dictEntryService.getDictEntries(50, null);
        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), MConventionalDict.class, null);
    }

    @RequestMapping(value = "/dictionaries/portal_notice_list", method = RequestMethod.GET)
    @ApiOperation(value = "通知公告类别清单", response = MConventionalDict.class)
    public MConventionalDict getPortalNoticeTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(55, code);
        return getDictModel(noticeType);
    }

    @RequestMapping(value = "/dictionaries/portal_notice_protal_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "通知公告云类别清单", response = MConventionalDict.class)
    public MConventionalDict getPortalNoticeProtalTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(56, code);
        return getDictModel(noticeType);
    }

    @RequestMapping(value = "/dictionaries/portal_resources_platform_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "资源平台类别类别清单", response = MConventionalDict.class)
    public MConventionalDict getPortalResourcesPlatformTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(57, code);
        return getDictModel(noticeType);
    }

    @RequestMapping(value = "/dictionaries/portal_resources_develop_lan_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "资源应用开发环境类别清单", response = MConventionalDict.class)
    public MConventionalDict getPortalResourcesDevelopLanTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(58, code);
        return getDictModel(noticeType);
    }

    @RequestMapping(value = "/dictionaries/portal_messageRemind_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "消息提醒类别清单", response = MConventionalDict.class)
    public MConventionalDict getMessageRemindTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(59, code);
        return getDictModel(noticeType);
    }

    @RequestMapping(value = "/dictionaries/portal_columnRequest_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "栏目请求方式清单", response = MConventionalDict.class)
    public MConventionalDict getColumnRequestTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(60, code);
        return getDictModel(noticeType);
    }

    @RequestMapping(value = "/dictionaries/medicalCard_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "就诊卡类型", response = MConventionalDict.class)
    public MConventionalDict getMedicalCardTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry noticeType = dictEntryService.getDictEntry(66, code);
        return getDictModel(noticeType);
    }


    @RequestMapping(value = "/dictionaries/dimension_main_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "指标主维度类型", response = MConventionalDict.class)
    MConventionalDict getDimensionMainTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        SystemDictEntry type = dictEntryService.getDictEntry(72, code);
        return getDictModel(type);
    };

    @RequestMapping(value = "/dictionaries/dimension_slave_type_list", method = RequestMethod.GET)
        @ApiOperation(value = "指标从维度类型", response = MConventionalDict.class)
        MConventionalDict getDimensionSlaveTypeList(
                @ApiParam(name = "code", value = "字典代码", defaultValue = "")
                @RequestParam(value = "code") String code){
            SystemDictEntry type = dictEntryService.getDictEntry(73, code);
            return getDictModel(type);
        };

        @RequestMapping(value = "/dictionaries/dimension_status_list", method = RequestMethod.GET)
        @ApiOperation(value = "指标维度状态", response = MConventionalDict.class)
        MConventionalDict getDimensionStatusList(
                @ApiParam(name = "code", value = "字典代码", defaultValue = "")
                @RequestParam(value = "code") String code)  {
            SystemDictEntry type = dictEntryService.getDictEntry(74, code);
            return getDictModel(type);
    };


    @RequestMapping(value = "/dictionaries/tj_data_source_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "指标统计数据资源", response = MConventionalDict.class)
    MConventionalDict getTjDataSourceTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        SystemDictEntry type = dictEntryService.getDictEntry(75, code);
        return getDictModel(type);
    };

    @RequestMapping(value = "/dictionaries/tj_data_save_list", method = RequestMethod.GET)
    @ApiOperation(value = "指标统计数据存储", response = MConventionalDict.class)
    MConventionalDict getTjDataSaveList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry type = dictEntryService.getDictEntry(76, code);
        return getDictModel(type);
    };

    @RequestMapping(value = "/dictionaries/tj_quota_exec_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "指标统计指标管理", response = MConventionalDict.class)
    MConventionalDict getTjQuotaExecTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry type = dictEntryService.getDictEntry(77, code);
        return getDictModel(type);
    };

    @RequestMapping(value = "/dictionaries/tj_quota_data_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "指标统计存储方式", response = MConventionalDict.class)
    MConventionalDict getTjQuotaDataLevelList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry type = dictEntryService.getDictEntry(77, code);
        return getDictModel(type);
    };

    @RequestMapping(value ="/GetAlldictionariesByDictId",method = RequestMethod.GET)
    @ApiOperation(value = "获取卡状态所有字典项")
    public ListResult GetAlldictionariesByDictId() throws Exception{
        long dictId=10;
        int page=0;
        int size=1000;
        ListResult re = new ListResult(page,size);
        Page<SystemDictEntry> cardList = dictEntryService.findByDictId(dictId, page,size);
        if(cardList!=null) {
            re.setDetailModelList(cardList.getContent());
            re.setTotalCount(cardList.getTotalPages());
        }
        return re;
    }



}
