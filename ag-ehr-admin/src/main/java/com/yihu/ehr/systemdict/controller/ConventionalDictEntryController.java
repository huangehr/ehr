package com.yihu.ehr.systemdict.controller;

import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.dict.SystemDictEntryModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RequestMapping(ApiVersion.Version1_0 +"/admin")
@RestController
public class ConventionalDictEntryController extends BaseController{

    @Autowired
    private ConventionalDictEntryClient dictEntryClient;

    @RequestMapping(value = "/dictionaries/app_catalog", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用类别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getAppCatalog(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getAppCatalog(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop  ;
    }

    @RequestMapping(value = "/dictionaries/app_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getAppStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getAppStatus(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/gender", method = RequestMethod.GET)
    @ApiOperation(value = "获取性别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getGender(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getGender(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/martial_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取婚姻状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getMartialStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getMartialStatus(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/nation", method = RequestMethod.GET)
    @ApiOperation(value = "获取国家字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getNation(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getNation(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/residence_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取人口居住类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getResidenceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getResidenceType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/org_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取组织机构类别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getOrgType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getOrgType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/settled_way", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构入驻方式字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getSettledWay(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getSettledWay(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/card_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getCardStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getCardStatus(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/card_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡类别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getCardType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getCardType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/request_state", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员请求消息状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getRequestState(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getRequestState(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/key_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取密钥类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getKeyType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getKeyType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/medical_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取医疗角色字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getMedicalRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getMedicalRole(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户角色字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getUserRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getUserRole(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getUserType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getUserType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/3rd_app", method = RequestMethod.GET)
    @ApiOperation(value = "获取连接的第三方应用字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getLoginAddress(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getLoginAddress(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/yes_no", method = RequestMethod.GET)
    @ApiOperation(value = "获取是否字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getYesNo(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getYesNo(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/adaption_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getAdapterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getAdapterType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/std_source_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getStdSourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getStdSourceType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }


    @RequestMapping(value = "/dictionaries/std_source_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getStdSourceTypeList(
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") String[] codes) {

        Envelop envelop = new Envelop();
        List<MConventionalDict> mConventionalDictList = dictEntryClient.getStdSourceTypeList(Arrays.asList(codes));
        List<SystemDictEntryModel> systemDictEntryModelList = new ArrayList<>();

        for (MConventionalDict mConventionalDict: mConventionalDictList){
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);
            systemDictEntryModelList.add(systemDictEntryModel);
        }

        envelop.setDetailModelList(systemDictEntryModelList);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getUserTypeList() {

        Envelop envelop = new Envelop();
        List<SystemDictEntryModel> systemDictEntryModelList = new ArrayList<>();

        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getUserTypeList();

        for (MConventionalDict mConventionalDict: mConventionalDicts){
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);
            systemDictEntryModelList.add(systemDictEntryModel);
        }

        envelop.setDetailModelList(systemDictEntryModelList);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取标签字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getTagsList() {

        Envelop envelop = new Envelop();
        List<SystemDictEntryModel> systemDictEntryModelList = new ArrayList<>();

        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getTagsList();

        for (MConventionalDict mConventionalDict: mConventionalDicts){
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);
            systemDictEntryModelList.add(systemDictEntryModel);
        }

        envelop.setDetailModelList(systemDictEntryModelList);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/record_data_sources", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案数据来源", response = MConventionalDict.class, produces = "application/json")
    public Envelop getRecordDataSourceList() {

        Envelop envelop = new Envelop();
        List<SystemDictEntryModel> systemDictEntryModelList = new ArrayList<>();

        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getRecordDataSourceList();

        for (MConventionalDict mConventionalDict: mConventionalDicts){
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);
            systemDictEntryModelList.add(systemDictEntryModel);
        }

        envelop.setDetailModelList(systemDictEntryModelList);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/drug_flag", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典处方标识", response = MConventionalDict.class, produces = "application/json")
    public Envelop getDrugFlag(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getDrugFlag(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/drug_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典类别", response = MConventionalDict.class, produces = "application/json")
    public Envelop getDrugType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code)  {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getDrugType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/indicator_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典类别", response = MConventionalDict.class, produces = "application/json")
    public Envelop getIndicatorType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code)  {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getIndicatorType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);

        envelop.setObj(systemDictEntryModel);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_source", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户来源字典项", response = MConventionalDict.class)
    public Envelop getUserSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getUserSource(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }


    @RequestMapping(value = "/dictionaries/resource_adapt_scheme", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源适配方案类别字典项", response = MConventionalDict.class)
    public Envelop getResourceAdaptScheme(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getResourceAdaptScheme(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/mete_data_field_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元字段类型字典项", response = MConventionalDict.class)
    public Envelop getMeteDataFieldType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getMeteDataFieldType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/resource_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源分类字典项", response = MConventionalDict.class)
    public Envelop getResourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getResourceType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/business_domain", method = RequestMethod.GET)
    @ApiOperation(value = "获取业务领域字典项", response = MConventionalDict.class)
    public Envelop getBusinessDomain(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getBusinessDomain(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/resource_access_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源访问方式字典项", response = MConventionalDict.class)
    public Envelop getResourceAccessType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getResourceAccessType(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/record_data_source", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案数据来源", response = MConventionalDict.class)
    public Envelop getRecordDataSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        MConventionalDict mConventionalDict = dictEntryClient.getRecordDataSource(code);
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        return envelop;
    }

    @RequestMapping(value = "/dictionaries/archives_audit_status", method = RequestMethod.GET)
    @ApiOperation(value = "档案关联审批状态", response = MConventionalDict.class)
    public Envelop getArchivesAuditStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getArchivesAuditStatus(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/archives_audit_status_list", method = RequestMethod.GET)
    @ApiOperation(value = "档案关联审批状态", response = MConventionalDict.class)
    public Envelop  getArchivesAuditStatusList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getArchivesAuditStatusList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/archives_manage_status", method = RequestMethod.GET)
    @ApiOperation(value = "档案管理状态", response = MConventionalDict.class)
    public Envelop getArchivesManageStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getArchivesManageStatus(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/archives_manage_status_list", method = RequestMethod.GET)
    @ApiOperation(value = "档案管理状态", response = MConventionalDict.class)
    public Envelop getArchivesManageStatusList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getArchivesManageStatusList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/application_source", method = RequestMethod.GET)
    @ApiOperation(value = "应用来源", response = MConventionalDict.class)
    public Envelop getApplicationSource(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApplicationSource(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/application_source_list", method = RequestMethod.GET)
    @ApiOperation(value = "应用来源", response = MConventionalDict.class)
    public Envelop getApplicationSourceList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApplicationSourceList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/application_menu_type", method = RequestMethod.GET)
    @ApiOperation(value = "应用菜单类型", response = MConventionalDict.class)
    public Envelop getApplicationMenuType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApplicationMenuType(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/application_menu_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "应用菜单类型", response = MConventionalDict.class)
    public Envelop getApplicationMenuTypeList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApplicationMenuTypeList();
        return convertDictToListModel(mConventionalDicts);
    }


    @RequestMapping(value = "/dictionaries/open_level", method = RequestMethod.GET)
    @ApiOperation(value = "开放等级", response = MConventionalDict.class)
    public Envelop  getOpenLevel(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getOpenLevel(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/open_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "开放等级", response = MConventionalDict.class)
    public Envelop  getOpenLevelList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getOpenLevelList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/audit_level", method = RequestMethod.GET)
    @ApiOperation(value = "审计等级", response = MConventionalDict.class)
    public Envelop  getAuditLevel(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getAuditLevel(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/audit_level_list", method = RequestMethod.GET)
    @ApiOperation(value = "审计等级", response = MConventionalDict.class)
    public Envelop  getAuditLevelList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getAuditLevelList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/role_type", method = RequestMethod.GET)
    @ApiOperation(value = "角色组分类", response = MConventionalDict.class)
    public Envelop  getRoleType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getRoleType(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/role_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "角色组分类", response = MConventionalDict.class)
    public Envelop  getRoleTypeList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getRoleTypeList();
        return convertDictToListModel(mConventionalDicts);
    }


    @RequestMapping(value = "/dictionaries/valid_identification", method = RequestMethod.GET)
    @ApiOperation(value = "有效标识", response = MConventionalDict.class)
    public Envelop  getValidIdentification(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getValidIdentification(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/valid_identification_list", method = RequestMethod.GET)
    @ApiOperation(value = "有效标识", response = MConventionalDict.class)
    public Envelop  getValidIdentificationList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getValidIdentificationList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/api_protocol", method = RequestMethod.GET)
    @ApiOperation(value = "API协议", response = MConventionalDict.class)
    public Envelop  getApiProtocol(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApiProtocol(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/api_protocol_list", method = RequestMethod.GET)
    @ApiOperation(value = "API协议", response = MConventionalDict.class)
    public Envelop  getApiProtocolList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiProtocolList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/api_method", method = RequestMethod.GET)
    @ApiOperation(value = "API方法", response = MConventionalDict.class)
    public Envelop  getApiMethod(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApiMethod(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/api_method_list", method = RequestMethod.GET)
    @ApiOperation(value = "API方法", response = MConventionalDict.class)
    public Envelop  getApiMethodList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiMethodList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/api_type", method = RequestMethod.GET)
    @ApiOperation(value = "API类型", response = MConventionalDict.class)
    public Envelop  getApiType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApiType(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/api_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "字典代码", response = MConventionalDict.class)
    public Envelop  getApiTypeList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiTypeList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_type", method = RequestMethod.GET)
    @ApiOperation(value = "API参数类型", response = MConventionalDict.class)
    public Envelop  getApiParameterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApiParameterType(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数类型", response = MConventionalDict.class)
    public Envelop  getApiParameterTypeList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiParameterTypeList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_type", method = RequestMethod.GET)
    @ApiOperation(value = "API参数数据类型", response = MConventionalDict.class)
    public Envelop  getApiParameterDataType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApiParameterDataType(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_type_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数数据类型", response = MConventionalDict.class)
    public Envelop  getApiParameterDataTypeList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiParameterDataTypeList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_required", method = RequestMethod.GET)
    @ApiOperation(value = "API参数必输标识", response = MConventionalDict.class)
    public Envelop  getApiParameterDataRequired(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        MConventionalDict mConventionalDict = dictEntryClient.getApiParameterDataRequired(code);
        return convertDictToModel(mConventionalDict);
    }

    @RequestMapping(value = "/dictionaries/api_parameter_data_required_list", method = RequestMethod.GET)
    @ApiOperation(value = "API参数必输标识", response = MConventionalDict.class)
    public Envelop  getApiParameterDataRequiredList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiParameterDataRequiredList();
        return convertDictToListModel(mConventionalDicts);
    }

    @RequestMapping(value = "/dictionaries/cda_type_browser_list", method = RequestMethod.GET)
    @ApiOperation(value = "浏览器用CDA类别清单", response = MConventionalDict.class)
    public Envelop getCdaTypeForBrowserList(){
        Collection<MConventionalDict> mConventionalDicts = dictEntryClient.getApiParameterDataRequiredList();
        return convertDictToListModel(mConventionalDicts);
    }

    private Envelop convertDictToModel(MConventionalDict mConventionalDict){
        Envelop envelop = new Envelop();
        SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);
        envelop.setObj(systemDictEntryModel);
        envelop.setSuccessFlg(true);
        return envelop;
    }
    private Envelop convertDictToListModel(Collection<MConventionalDict> mConventionalDicts){
        Envelop envelop = new Envelop();
        List<SystemDictEntryModel> systemDictEntryModelList = new ArrayList<>();
        for (MConventionalDict mConventionalDict: mConventionalDicts){
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict,SystemDictEntryModel.class);
            systemDictEntryModelList.add(systemDictEntryModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(systemDictEntryModelList);
        return envelop  ;
    }
}
