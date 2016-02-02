package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.dict.service.common.*;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
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
public class ConventionalDictController extends BaseRestController {


    @Autowired
    private ConventionalDictEntry baseAbstractDictEntry;


    MConventionalDict getDictModel(Object dict){
        MConventionalDict dictModel = new MConventionalDict();
        BeanUtils.copyProperties(dict,dictModel);
        return dictModel;
    }

    @RequestMapping(value = "/appCatalog", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getAppCatalog(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict appCatalog = baseAbstractDictEntry.getAppCatalog(code);
        return getDictModel(appCatalog);
    }

    @RequestMapping(value = "/appStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getAppStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict appStatus = baseAbstractDictEntry.getAppStatus(code);
        return getDictModel(appStatus);
    }

    @RequestMapping(value = "/gender", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getGender(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict gender = baseAbstractDictEntry.getGender(code);
        return getDictModel(gender);
    }

    @RequestMapping(value = "/martialStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getMartialStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict martialStatus = baseAbstractDictEntry.getMartialStatus(code);
        return getDictModel(martialStatus);
    }

    @RequestMapping(value = "/nation", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getNation(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict nation = baseAbstractDictEntry.getNation(code);
        return getDictModel(nation);
    }

    @RequestMapping(value = "/residenceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getResidenceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict residenceType = baseAbstractDictEntry.getResidenceType(code);
        return getDictModel(residenceType);
    }

    @RequestMapping(value = "/orgType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getOrgType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict orgType = baseAbstractDictEntry.getOrgType(code);
        return getDictModel(orgType);
    }

    @RequestMapping(value = "/settledWay", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getSettledWay(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict settledWay = baseAbstractDictEntry.getSettledWay(code);
        return getDictModel(settledWay);
    }

    @RequestMapping(value = "/cardStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getCardStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict cardStatus = baseAbstractDictEntry.getCardStatus(code);
        return getDictModel(cardStatus);
    }

    @RequestMapping(value = "/cardType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getCardType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict cardType = baseAbstractDictEntry.getCardType(code);
        return getDictModel(cardType);
    }

    @RequestMapping(value = "/requestState", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getRequestState(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict requestState = baseAbstractDictEntry.getRequestState(code);
        return getDictModel(requestState);
    }

    @RequestMapping(value = "/keyType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getKeyType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict keyType = baseAbstractDictEntry.getKeyType(code);
        return getDictModel(keyType);
    }

    @RequestMapping(value = "/medicalRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getMedicalRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict medicalRole =  baseAbstractDictEntry.getMedicalRole(code);
        return getDictModel(medicalRole);
    }

    @RequestMapping(value = "/userRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getUserRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict userRole = baseAbstractDictEntry.getUserRole(code);
        return getDictModel(userRole);
    }

    @RequestMapping(value = "/userType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getUserType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict userType = baseAbstractDictEntry.getUserType(code);
        return getDictModel(userType);
    }

    @RequestMapping(value = "/loginAddress", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getLoginAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict loginAddress = baseAbstractDictEntry.getLoginAddress(code);
        return getDictModel(loginAddress);
    }

    //...............................................................

    @RequestMapping(value = "/userTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<MConventionalDict> getUserTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        List<ConventionalDict> list = baseAbstractDictEntry.getUserTypeList();
        List<MConventionalDict> listnew = new ArrayList<>();
        for(ConventionalDict userType : list){
            MConventionalDict dictModel = new MConventionalDict();
            BeanUtils.copyProperties(userType,dictModel);
            listnew.add(dictModel);
        }
        return listnew;
    }

    @RequestMapping(value = "/tagsList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<MConventionalDict> getTagsList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        List<ConventionalDict> list = baseAbstractDictEntry.getTagsList();
        List<MConventionalDict> listnew = new ArrayList<>();
        for(ConventionalDict tags : list){
            MConventionalDict dictModel = new MConventionalDict();
            BeanUtils.copyProperties(tags,dictModel);
            listnew.add(dictModel);
        }
        return listnew;
    }

    //...............................................................

    @RequestMapping(value = "/yesNo", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getYesNo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {
        ConventionalDict yesNo = baseAbstractDictEntry.getYesNo(code);
        return getDictModel(yesNo);
    }

    @RequestMapping(value = "/adapterType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getAdapterType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict adapterType = baseAbstractDictEntry.getAdapterType(code);
        return getDictModel(adapterType);
    }


    @RequestMapping(value = "/stdSourceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MConventionalDict getStdSourceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        ConventionalDict stdSourceType = baseAbstractDictEntry.getStdSourceType(code);
        return getDictModel(stdSourceType);
    }


    @RequestMapping(value = "/stdSourceTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<MConventionalDict> getStdSourceTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") String[] codes) {
        List<ConventionalDict> list = baseAbstractDictEntry.getStdSourceTypeList(codes);
        List<MConventionalDict> listnew = new ArrayList<>();
        for(ConventionalDict stdSourceType : list){
            MConventionalDict dictModel = new MConventionalDict();
            BeanUtils.copyProperties(stdSourceType,dictModel);
            listnew.add(dictModel);
        }
        return listnew;
    }





}
