package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictService;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 惯用字典接口，用于快速提取常用的字典项目。
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "conventional_dictionaries", description = "惯用字典接口，对于常见的字典可直接获取字典项", tags = {"惯用字典接口"})
public class ConventionalDictController extends BaseRestController {

    @Autowired
    private SystemDictService baseAbstractDictEntry;

    MConventionalDict getDictModel(Object dict) {
        MConventionalDict dictModel = new MConventionalDict();
        BeanUtils.copyProperties(dict, dictModel);
        return dictModel;
    }

    @RequestMapping(value = "/dictionaries/app_catalog", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用类别字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getAppCatalog(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry appCatalog = baseAbstractDictEntry.getConvertionalDict(1, code);
        return getDictModel(appCatalog);
    }

    @RequestMapping(value = "/dictionaries/app_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用状态字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getAppStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry appStatus = baseAbstractDictEntry.getConvertionalDict(2, code);
        return getDictModel(appStatus);
    }

    @RequestMapping(value = "/dictionaries/gender", method = RequestMethod.GET)
    @ApiOperation(value = "获取性别字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getGender(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry gender = baseAbstractDictEntry.getConvertionalDict(3, code);
        return getDictModel(gender);
    }

    @RequestMapping(value = "/dictionaries/martial_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取婚姻状态字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getMartialStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry martialStatus = baseAbstractDictEntry.getConvertionalDict(4, code);
        return getDictModel(martialStatus);
    }

    @RequestMapping(value = "/dictionaries/nation", method = RequestMethod.GET)
    @ApiOperation(value = "获取国家字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getNation(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry nation = baseAbstractDictEntry.getConvertionalDict(5, code);
        return getDictModel(nation);
    }

    @RequestMapping(value = "/dictionaries/residence_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取人口居住类型字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getResidenceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry residenceType = baseAbstractDictEntry.getConvertionalDict(6, code);
        return getDictModel(residenceType);
    }

    @RequestMapping(value = "/dictionaries/org_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取组织机构类别字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getOrgType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry orgType = baseAbstractDictEntry.getConvertionalDict(7, code);
        return getDictModel(orgType);
    }

    @RequestMapping(value = "/dictionaries/settled_way", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构入驻方式字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getSettledWay(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry settledWay = baseAbstractDictEntry.getConvertionalDict(8, code);
        return getDictModel(settledWay);
    }

    @RequestMapping(value = "/dictionaries/card_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡状态字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getCardStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry cardStatus = baseAbstractDictEntry.getConvertionalDict(9, code);
        return getDictModel(cardStatus);
    }

    @RequestMapping(value = "/dictionaries/card_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡类别字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getCardType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry cardType = baseAbstractDictEntry.getConvertionalDict(10, code);
        return getDictModel(cardType);
    }

    @RequestMapping(value = "/dictionaries/request_state", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员请求消息状态字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getRequestState(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry requestState = baseAbstractDictEntry.getConvertionalDict(11, code);
        return getDictModel(requestState);
    }

    @RequestMapping(value = "/dictionaries/key_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取密钥类型字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getKeyType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry keyType = baseAbstractDictEntry.getConvertionalDict(12, code);
        return getDictModel(keyType);
    }

    @RequestMapping(value = "/dictionaries/medical_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取医疗角色字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getMedicalRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry medicalRole = baseAbstractDictEntry.getConvertionalDict(13, code);
        return getDictModel(medicalRole);
    }

    @RequestMapping(value = "/dictionaries/user_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户角色字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getUserRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry userRole = baseAbstractDictEntry.getConvertionalDict(14, code);
        return getDictModel(userRole);
    }

    @RequestMapping(value = "/dictionaries/user_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getUserType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry userType = baseAbstractDictEntry.getConvertionalDict(15, code);
        return getDictModel(userType);
    }

    @RequestMapping(value = "/dictionaries/3rd_app", method = RequestMethod.GET)
    @ApiOperation(value = "获取连接的第三方应用字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getLoginAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry loginAddress = baseAbstractDictEntry.getConvertionalDict(20, code);
        return getDictModel(loginAddress);
    }

    @RequestMapping(value = "/dictionaries/yes_no", method = RequestMethod.GET)
    @ApiOperation(value = "获取是否字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getYesNo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {
        String resultCode = code ? "True" : "False";

        SystemDictEntry yesNo = baseAbstractDictEntry.getConvertionalDict(18, resultCode);
        return getDictModel(yesNo);
    }

    @RequestMapping(value = "/dictionaries/adaption_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配类型字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getAdapterType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry adapterType = baseAbstractDictEntry.getConvertionalDict(21, code);
        return getDictModel(adapterType);
    }

    @RequestMapping(value = "/dictionaries/std_source_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源字典项", response = MConventionalDict.class, produces = "application/json")
    public MConventionalDict getStdSourceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SystemDictEntry stdSourceType = baseAbstractDictEntry.getConvertionalDict(22, code);
        return getDictModel(stdSourceType);
    }


    @RequestMapping(value = "/dictionaries/std_source_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源类型字典项", response = MConventionalDict.class, produces = "application/json")
    public List<MConventionalDict> getStdSourceTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") String[] codes) {
        List<SystemDictEntry> list = baseAbstractDictEntry.getConvertionalDicts(22, codes);
        List<MConventionalDict> listnew = new ArrayList<>();
        for (SystemDictEntry stdSourceType : list) {
            MConventionalDict dictModel = new MConventionalDict();
            BeanUtils.copyProperties(stdSourceType, dictModel);
            listnew.add(dictModel);
        }
        return listnew;
    }

    @RequestMapping(value = "/dictionaries/user_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Collection<MConventionalDict> getUserTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        List<SystemDictEntry> list = baseAbstractDictEntry.getDictEntries(15, null);

        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), null);
    }

    @RequestMapping(value = "/dictionaries/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取标签字典项", response = MConventionalDict.class, produces = "application/json")
    public Collection<MConventionalDict> getTagsList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        List<SystemDictEntry> list = baseAbstractDictEntry.getDictEntries(17, null);

        return convertToModels(list, new ArrayList<MConventionalDict>(list.size()), null);
    }
}
