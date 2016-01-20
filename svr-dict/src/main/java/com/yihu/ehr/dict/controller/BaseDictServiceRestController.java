package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.dict.service.*;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/conventional_dict")
@Api(protocols = "https", value = "conventional_dict", description = "通用字典接口", tags = {"通用字典接口"})
public class BaseDictServiceRestController extends BaseRestController {


    @Autowired
    private ConventionalDictEntry baseAbstractDictEntry;

    @RequestMapping(value = "/appCatalog", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public AppCatalog getAppCatalog(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        AppCatalog appCatalog = baseAbstractDictEntry.getAppCatalog(code);
        return appCatalog;
    }

    @RequestMapping(value = "/appStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public AppStatus getAppStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getAppStatus(code);
    }

    @RequestMapping(value = "/gender", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public Gender getGender(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getGender(code);
    }

    @RequestMapping(value = "/martialStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MartialStatus getMartialStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getMartialStatus(code);
    }

    @RequestMapping(value = "/nation", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public Nation getNation(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getNation(code);
    }

    @RequestMapping(value = "/residenceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public ResidenceType getResidenceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getResidenceType(code);
    }

    @RequestMapping(value = "/orgType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public OrgType getOrgType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getOrgType(code);
    }

    @RequestMapping(value = "/settledWay", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public SettledWay getSettledWay(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getSettledWay(code);
    }

    @RequestMapping(value = "/cardStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public CardStatus getCardStatus(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getCardStatus(code);
    }

    @RequestMapping(value = "/cardType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public CardType getCardType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getCardType(code);
    }

    @RequestMapping(value = "/requestState", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public RequestState getRequestState(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getRequestState(code);
    }

    @RequestMapping(value = "/keyType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public KeyType getKeyType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getKeyType(code);
    }

    @RequestMapping(value = "/medicalRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MedicalRole getMedicalRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getMedicalRole(code);
    }

    @RequestMapping(value = "/userRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public UserRole getUserRole(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getUserRole(code);
    }

    @RequestMapping(value = "/userType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public UserType getUserType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getUserType(code);
    }

    @RequestMapping(value = "/loginAddress", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public LoginAddress getLoginAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getLoginAddress(code);
    }

    //...............................................................

    @RequestMapping(value = "/userTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<UserType> getUserTypeList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        return baseAbstractDictEntry.getUserTypeList();
    }

    @RequestMapping(value = "/tagsList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<Tags> getTagsList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        return baseAbstractDictEntry.getTagsList();
    }

    //...............................................................

    @RequestMapping(value = "/yesNo", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public YesNo getYesNo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {
        return baseAbstractDictEntry.getYesNo(code);
    }

    @RequestMapping(value = "/adapterType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public AdapterType getAdapterType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getAdapterType(code);
    }


    @RequestMapping(value = "/stdSourceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public StdSourceType getStdSourceType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getStdSourceType(code);
    }





}
