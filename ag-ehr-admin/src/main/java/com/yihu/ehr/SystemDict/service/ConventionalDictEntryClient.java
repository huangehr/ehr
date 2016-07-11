package com.yihu.ehr.systemdict.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient(name=MicroServices.Dictionary)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
 interface ConventionalDictEntryClient {

    @RequestMapping(value = "/dictionaries/app_catalog", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用类别字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getAppCatalog(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/app_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用状态字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getAppStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/gender", method = RequestMethod.GET)
    @ApiOperation(value = "获取性别字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getGender(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/martial_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取婚姻状态字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getMartialStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/nation", method = RequestMethod.GET)
    @ApiOperation(value = "获取国家字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getNation(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/residence_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取人口居住类型字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getResidenceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/org_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取组织机构类别字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getOrgType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/settled_way", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构入驻方式字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getSettledWay(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/card_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡状态字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getCardStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/card_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡类别字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getCardType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/request_state", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员请求消息状态字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getRequestState(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/key_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取密钥类型字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getKeyType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/medical_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取医疗角色字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getMedicalRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/user_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户角色字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getUserRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/user_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getUserType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/3rd_app", method = RequestMethod.GET)
    @ApiOperation(value = "获取连接的第三方应用字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getLoginAddress(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/yes_no", method = RequestMethod.GET)
    @ApiOperation(value = "获取是否字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getYesNo(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code);

    @RequestMapping(value = "/dictionaries/adaption_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配类型字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getAdapterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/std_source_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getStdSourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/family_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭关系字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getFamilyRelationship(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);


    @RequestMapping(value = "/dictionaries/std_source_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源类型字典项", response = MConventionalDict.class, produces = "application/json")
    List<MConventionalDict> getStdSourceTypeList(
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") List<String> codes);

    @RequestMapping(value = "/dictionaries/user_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    Collection<MConventionalDict> getUserTypeList();

    @RequestMapping(value = "/dictionaries/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取标签字典项", response = MConventionalDict.class, produces = "application/json")
    Collection<MConventionalDict> getTagsList();

    @RequestMapping(value = "/dictionaries/record_data_sources", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案数据来源", response = MConventionalDict.class, produces = "application/json")
    Collection<MConventionalDict> getRecordDataSourceList();

    @RequestMapping(value = "/dictionaries/drug_flag", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典处方标识", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getDrugFlag(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/drug_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典类别", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getDrugType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/indicator_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典类别", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getIndicatorType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/user_source", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典类别", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getUserSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/resource_adapt_scheme", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源适配方案类别字典项", response = MConventionalDict.class)
    MConventionalDict getResourceAdaptScheme(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/mete_data_field_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元字段类型字典项", response = MConventionalDict.class)
    MConventionalDict getMeteDataFieldType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/resource_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源分类字典项", response = MConventionalDict.class)
    MConventionalDict getResourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/business_domain", method = RequestMethod.GET)
    @ApiOperation(value = "获取业务领域字典项", response = MConventionalDict.class)
    MConventionalDict getBusinessDomain(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/resource_access_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源访问方式字典项", response = MConventionalDict.class)
    MConventionalDict getResourceAccessType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/logical_relationship", method = RequestMethod.GET)
    @ApiOperation(value = "获取逻辑关系字典项", response = MConventionalDict.class)
    MConventionalDict getLogicalRelationship(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/record_data_source", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案数据来源", response = MConventionalDict.class)
    MConventionalDict getRecordDataSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/archives_audit_status", method = RequestMethod.GET)
    @ApiOperation(value = "档案关联审批状态", response = MConventionalDict.class)
     MConventionalDict getArchivesAuditStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/archives_audit_status_list", method = RequestMethod.GET)
    @ApiOperation(value = "档案关联审批状态", response = MConventionalDict.class)
     Collection<MConventionalDict> getArchivesAuditStatusList();

    @RequestMapping(value = "/dictionaries/archives_manage_status", method = RequestMethod.GET)
    @ApiOperation(value = "档案管理状态", response = MConventionalDict.class)
     MConventionalDict getArchivesManageStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/archives_manage_status_list", method = RequestMethod.GET)
    @ApiOperation(value = "档案管理状态", response = MConventionalDict.class)
     Collection<MConventionalDict> getArchivesManageStatusList();

    @RequestMapping(value = "/dictionaries/application_source", method = RequestMethod.GET)
    @ApiOperation(value = "应用来源", response = MConventionalDict.class)
     MConventionalDict getApplicationSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/application_source_list", method = RequestMethod.GET)
    @ApiOperation(value = "应用来源", response = MConventionalDict.class)
     Collection<MConventionalDict> getApplicationSourceList();

    @RequestMapping(value = "/dictionaries/application_menu_type", method = RequestMethod.GET)
    @ApiOperation(value = "应用菜单类型", response = MConventionalDict.class)
     MConventionalDict getApplicationMenuType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/application_menu_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "应用菜单类型", response = MConventionalDict.class)
     Collection<MConventionalDict> getApplicationMenuTypeList();


    @RequestMapping(value = "/dictionaries/open_level", method = RequestMethod.GET)
    @ApiOperation(value = "开放等级", response = MConventionalDict.class)
     MConventionalDict getOpenLevel(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/open_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "开放等级", response = MConventionalDict.class)
     Collection<MConventionalDict> getOpenLevelList();

    @RequestMapping(value = "/dictionaries/audit_level", method = RequestMethod.GET)
    @ApiOperation(value = "审计等级", response = MConventionalDict.class)
     MConventionalDict getAuditLevel(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/audit_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "审计等级", response = MConventionalDict.class)
     Collection<MConventionalDict> getAuditLevelList();

    @RequestMapping(value = "/dictionaries/role_type", method = RequestMethod.GET)
    @ApiOperation(value = "角色组分类", response = MConventionalDict.class)
     MConventionalDict getRoleType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/role_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "角色组分类", response = MConventionalDict.class)
     Collection<MConventionalDict> getRoleTypeList();


    @RequestMapping(value = "/dictionaries/valid_identification", method = RequestMethod.GET)
    @ApiOperation(value = "有效标识", response = MConventionalDict.class)
     MConventionalDict getValidIdentification(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/valid_identification_list", method = RequestMethod.GET)
    @ApiOperation(value = "有效标识", response = MConventionalDict.class)
     Collection<MConventionalDict> getValidIdentificationList();

    @RequestMapping(value = "/dictionaries/api_protocol", method = RequestMethod.GET)
    @ApiOperation(value = "API协议", response = MConventionalDict.class)
     MConventionalDict getApiProtocol(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/api_protocol_list", method = RequestMethod.GET)
    @ApiOperation(value = "API协议", response = MConventionalDict.class)
     Collection<MConventionalDict> getApiProtocolList();

    @RequestMapping(value = "/dictionaries/api_method", method = RequestMethod.GET)
    @ApiOperation(value = "API方法", response = MConventionalDict.class)
     MConventionalDict getApiMethod(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/api_method_list", method = RequestMethod.GET)
    @ApiOperation(value = "API方法", response = MConventionalDict.class)
     Collection<MConventionalDict> getApiMethodList();

    @RequestMapping(value = "/dictionaries/api_type", method = RequestMethod.GET)
    @ApiOperation(value = "API类型", response = MConventionalDict.class)
     MConventionalDict getApiType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/api_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "字典代码", response = MConventionalDict.class)
     Collection<MConventionalDict> getApiTypeList();

    @RequestMapping(value = "/dictionaries/api_parameter_type", method = RequestMethod.GET)
    @ApiOperation(value = "API参数类型", response = MConventionalDict.class)
     MConventionalDict getApiParameterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/api_parameter_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数类型", response = MConventionalDict.class)
     Collection<MConventionalDict> getApiParameterTypeList();

    @RequestMapping(value = "/dictionaries/api_parameter_data_type", method = RequestMethod.GET)
    @ApiOperation(value = "API参数数据类型", response = MConventionalDict.class)
     MConventionalDict getApiParameterDataType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/api_parameter_data_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数数据类型", response = MConventionalDict.class)
     Collection<MConventionalDict> getApiParameterDataTypeList();

    @RequestMapping(value = "/dictionaries/api_parameter_data_required", method = RequestMethod.GET)
    @ApiOperation(value = "API参数必输标识", response = MConventionalDict.class)
     MConventionalDict getApiParameterDataRequired(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/api_parameter_data_required_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数必输标识", response = MConventionalDict.class)
     Collection<MConventionalDict> getApiParameterDataRequiredList();
}
