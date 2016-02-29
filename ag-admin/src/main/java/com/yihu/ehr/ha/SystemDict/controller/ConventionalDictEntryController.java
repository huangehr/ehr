package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.Array_ListUtil;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RequestMapping(ApiVersion.Version1_0 +"/admin")
@RestController
public class ConventionalDictEntryController {

    @Autowired
    private ConventionalDictEntryClient dictEntryClient;

    @RequestMapping(value = "/dictionaries/app_catalog", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用类别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getAppCatalog(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getAppCatalog(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/app_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取应用状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getAppStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getAppStatus(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/gender", method = RequestMethod.GET)
    @ApiOperation(value = "获取性别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getGender(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getGender(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/martial_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取婚姻状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getMartialStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getMartialStatus(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/nation", method = RequestMethod.GET)
    @ApiOperation(value = "获取国家字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getNation(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getNation(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/residence_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取人口居住类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getResidenceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getResidenceType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/org_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取组织机构类别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getOrgType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getOrgType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/settled_way", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构入驻方式字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getSettledWay(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getSettledWay(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/card_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getCardStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getCardStatus(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/card_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取卡类别字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getCardType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getCardType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/request_state", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员请求消息状态字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getRequestState(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getRequestState(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/key_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取密钥类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getKeyType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getKeyType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/medical_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取医疗角色字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getMedicalRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getMedicalRole(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_role", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户角色字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getUserRole(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getUserRole(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getUserType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getUserType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/3rd_app", method = RequestMethod.GET)
    @ApiOperation(value = "获取连接的第三方应用字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getLoginAddress(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getLoginAddress(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/yes_no", method = RequestMethod.GET)
    @ApiOperation(value = "获取是否字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getYesNo(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") boolean code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getYesNo(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/adaption_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getAdapterType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getAdapterType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/std_source_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getStdSourceType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict = dictEntryClient.getStdSourceType(code);

        envelop.setObj(mConventionalDict);

        return envelop;
    }


    @RequestMapping(value = "/dictionaries/std_source_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Envelop getStdSourceTypeList(
            @ApiParam(name = "codes", value = "字典代码", defaultValue = "")
            @RequestParam(value = "codes") String[] codes) {

        Envelop envelop = new Envelop();

        List<MConventionalDict> mConventionalDictList = dictEntryClient.getStdSourceTypeList(Array_ListUtil.getList(codes));

        envelop.setDetailModelList(mConventionalDictList);

        return envelop;
    }

    @RequestMapping(value = "/dictionaries/user_types", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户类型字典项", response = MConventionalDict.class, produces = "application/json")
    public Collection<MConventionalDict> getUserTypeList() {
        return dictEntryClient.getUserTypeList();
    }

    @RequestMapping(value = "/dictionaries/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取标签字典项", response = MConventionalDict.class, produces = "application/json")
    public Collection<MConventionalDict> getTagsList() {
        return dictEntryClient.getTagsList();
    }
}
