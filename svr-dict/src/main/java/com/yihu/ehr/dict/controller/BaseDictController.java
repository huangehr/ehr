package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.dict.service.common.*;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.util.beanUtil.BeanUtils;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/conventional_dict")
@Api(protocols = "https", value = "conventional_dict", description = "通用字典接口", tags = {"通用字典接口"})
public class BaseDictController extends BaseRestController {


    @Autowired
    private ConventionalDictEntry baseAbstractDictEntry;


    MBaseDict getDictModel(MBaseDict dict){
        MBaseDict dictModel = BeanUtils.copyModelToVo(MBaseDict.class,dict);
        return dictModel;
    }

    @RequestMapping(value = "/appCatalog", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getAppCatalog(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        AppCatalog appCatalog = baseAbstractDictEntry.getAppCatalog(code);
        return getDictModel(appCatalog);
    }

    @RequestMapping(value = "/appStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getAppStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        AppStatus appStatus = baseAbstractDictEntry.getAppStatus(code);
        return getDictModel(appStatus);
    }

    @RequestMapping(value = "/gender", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getGender(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Gender gender = baseAbstractDictEntry.getGender(code);
        return getDictModel(gender);
    }

    @RequestMapping(value = "/martialStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getMartialStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        MartialStatus martialStatus = baseAbstractDictEntry.getMartialStatus(code);
        return getDictModel(martialStatus);
    }

    @RequestMapping(value = "/nation", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getNation(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        Nation nation = baseAbstractDictEntry.getNation(code);
        return getDictModel(nation);
    }

    @RequestMapping(value = "/residenceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getResidenceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ResidenceType residenceType = baseAbstractDictEntry.getResidenceType(code);
        return getDictModel(residenceType);
    }

    @RequestMapping(value = "/orgType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getOrgType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        OrgType orgType = baseAbstractDictEntry.getOrgType(code);
        return getDictModel(orgType);
    }

    @RequestMapping(value = "/settledWay", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getSettledWay(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        SettledWay settledWay = baseAbstractDictEntry.getSettledWay(code);
        return getDictModel(settledWay);
    }

    @RequestMapping(value = "/cardStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getCardStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        CardStatus cardStatus = baseAbstractDictEntry.getCardStatus(code);
        return getDictModel(cardStatus);
    }

    @RequestMapping(value = "/cardType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getCardType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        CardType cardType = baseAbstractDictEntry.getCardType(code);
        return getDictModel(cardType);
    }

    @RequestMapping(value = "/requestState", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getRequestState(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        RequestState requestState = baseAbstractDictEntry.getRequestState(code);
        return getDictModel(requestState);
    }

    @RequestMapping(value = "/keyType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getKeyType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        KeyType keyType = baseAbstractDictEntry.getKeyType(code);
        return getDictModel(keyType);
    }

    @RequestMapping(value = "/medicalRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getMedicalRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        MedicalRole medicalRole =  baseAbstractDictEntry.getMedicalRole(code);
        return getDictModel(medicalRole);
    }

    @RequestMapping(value = "/userRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getUserRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        UserRole userRole = baseAbstractDictEntry.getUserRole(code);
        return getDictModel(userRole);
    }

    @RequestMapping(value = "/userType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getUserType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        UserType userType = baseAbstractDictEntry.getUserType(code);
        return getDictModel(userType);
    }

    @RequestMapping(value = "/loginAddress", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getLoginAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        LoginAddress loginAddress = baseAbstractDictEntry.getLoginAddress(code);
        return getDictModel(loginAddress);
    }

    //...............................................................

    @RequestMapping(value = "/userTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<MBaseDict> getUserTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        List<UserType> list = baseAbstractDictEntry.getUserTypeList();
        List<MBaseDict> listnew = new ArrayList<>();
        for(UserType userType : list){
            MBaseDict dictModel = BeanUtils.copyModelToVo(MBaseDict.class,userType);
            listnew.add(dictModel);
        }
        return listnew;
    }

    @RequestMapping(value = "/tagsList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<MBaseDict> getTagsList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        List<Tags> list = baseAbstractDictEntry.getTagsList();
        List<MBaseDict> listnew = new ArrayList<>();
        for(Tags tags : list){
            MBaseDict dictModel = BeanUtils.copyModelToVo(MBaseDict.class,tags);
            listnew.add(dictModel);
        }
        return listnew;
    }

    //...............................................................

    @RequestMapping(value = "/yesNo", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getYesNo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {
        YesNo yesNo = baseAbstractDictEntry.getYesNo(code);
        return getDictModel(yesNo);
    }

    @RequestMapping(value = "/adapterType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getAdapterType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        AdapterType adapterType = baseAbstractDictEntry.getAdapterType(code);
        return getDictModel(adapterType);
    }


    @RequestMapping(value = "/stdSourceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MBaseDict getStdSourceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        StdSourceType stdSourceType = baseAbstractDictEntry.getStdSourceType(code);
        return getDictModel(stdSourceType);
    }


    @RequestMapping(value = "/stdSourceTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<MBaseDict> getStdSourceTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") String[] codes) {
        List<StdSourceType> list = baseAbstractDictEntry.getStdSourceTypeList(codes);
        List<MBaseDict> listnew = new ArrayList<>();
        for(StdSourceType stdSourceType : list){
            MBaseDict dictModel = BeanUtils.copyModelToVo(MBaseDict.class,stdSourceType);
            listnew.add(dictModel);
        }
        return listnew;
    }





}
