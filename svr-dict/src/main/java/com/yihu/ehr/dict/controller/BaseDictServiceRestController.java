package com.yihu.ehr.dict.controller;

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
@RequestMapping("/conventional_dict_service")
@Api(protocols = "https", value = "conventional_dict_service", description = "通用字典接口", tags = {"通用字典接口"})
public class BaseDictServiceRestController extends BaseRestController {

    @Autowired
    private ConventionalDictEntry baseAbstractDictEntry;

    @RequestMapping(value = "/getAppCatalog", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public AppCatalog getAppCatalog(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        AppCatalog appCatalog = baseAbstractDictEntry.getAppCatalog(code);
        return appCatalog;
    }

    @RequestMapping(value = "/getAppStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public AppStatus getAppStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getAppStatus(code);
    }

    @RequestMapping(value = "/getGender", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public Gender getGender(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getGender(code);
    }

    @RequestMapping(value = "/getMartialStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MartialStatus getMartialStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getMartialStatus(code);
    }

    @RequestMapping(value = "/getNation", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public Nation getNation(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getNation(code);
    }

    @RequestMapping(value = "/getResidenceType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public ResidenceType getResidenceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getResidenceType(code);
    }

    @RequestMapping(value = "/getOrgType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public OrgType getOrgType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getOrgType(code);
    }

    @RequestMapping(value = "/getSettledWay", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public SettledWay getSettledWay(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getSettledWay(code);
    }

    @RequestMapping(value = "/getCardStatus", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public CardStatus getCardStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getCardStatus(code);
    }

    @RequestMapping(value = "/getCardType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public CardType getCardType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getCardType(code);
    }

    @RequestMapping(value = "/getRequestState", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public RequestState getRequestState(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getRequestState(code);
    }

    @RequestMapping(value = "/getKeyType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public KeyType getKeyType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getKeyType(code);
    }

    @RequestMapping(value = "/getMedicalRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public MedicalRole getMedicalRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getMedicalRole(code);
    }

    @RequestMapping(value = "/getUserRole", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public UserRole getUserRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getUserRole(code);
    }

    @RequestMapping(value = "/getUserType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public UserType getUserType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getUserType(code);
    }

    @RequestMapping(value = "/getLoginAddress", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public LoginAddress getLoginAddress(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getLoginAddress(code);
    }

    //...............................................................

    @RequestMapping(value = "/getUserTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<UserType> getUserTypeList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getUserTypeList();
    }

    @RequestMapping(value = "/getTagsList", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public List<Tags> getTagsList(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getTagsList();
    }

    //...............................................................

    @RequestMapping(value = "/getYesNo", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public YesNo getYesNo(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {
        return baseAbstractDictEntry.getYesNo(code);
    }

    @RequestMapping(value = "/getAdapterType", method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取字典")
    public AdapterType getAdapterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {
        return baseAbstractDictEntry.getAdapterType(code);
    }


}
