package com.yihu.ehr.ha.organization.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.organization.service.OrganizationClient;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/organization")
@RestController
public class OrganizationController extends BaseRestController {

    @Autowired
    private OrganizationClient orgClient;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Object searchOrgs(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "fullName", value = "全称")
            @RequestParam(value = "fullName") String fullName,
            @ApiParam(name = "settledWay", value = "接入方式", defaultValue = "")
            @RequestParam(value = "settledWay") String settledWay,
            @ApiParam(name = "orgType", value = "机构类型")
            @RequestParam(value = "orgType") String orgType,
            @ApiParam(name = "province", value = "省、自治区、直辖市")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市、自治州、自治县、县")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "区/县")
            @RequestParam(value = "district") String district,
            @ApiParam(name = "page", value = "页面号，从0开始", defaultValue = "0")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "页面记录数", defaultValue = "10")
            @RequestParam(value = "rows") int rows) {

        return orgClient.searchOrgs(apiVersion, orgCode, fullName, settledWay, orgType, province, city, district, page, rows);

    }

    /**
     * 删除机构
     *
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public Object deleteOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {

        return orgClient.deleteOrg(apiVersion, orgCode);
    }


    /**
     * 激活机构
     *
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/activity", method = RequestMethod.PUT)
    public Object activity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "activityFlag", value = "状态", defaultValue = "")
            @RequestParam(value = "activityFlag") String activityFlag) {

        return orgClient.activity(apiVersion, orgCode, activityFlag);
    }


    /**
     * 跟新机构
     *
     * @param apiVersion
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Object updateOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            String orgModelJsonData) throws Exception {

        return orgClient.updateOrg(apiVersion, orgModelJsonData);
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object getOrgByCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {

        return orgClient.getOrgByCode(apiVersion, orgCode);
    }

    /**
     * 根据code获取机构信息
     *
     * @param orgCode
     * @return
     */
    @ApiOperation(value = "根据地址代码获取model")
    @RequestMapping(value = "/org_model", method = RequestMethod.GET)
    public Object getOrgModel(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) {

        return orgClient.getOrgModel(apiVersion, orgCode);
    }

    /**
     * 根据name获取机构ids
     *
     * @param name
     * @return
     */
    @ApiOperation(value = "根据地名称取机构ids")
    @RequestMapping(value = "/name", method = RequestMethod.GET)
    public Object getIdsByName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name) {

        return orgClient.getIdsByName(apiVersion, name);
    }


    /**
     * 根据地址获取机构
     *
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Object getOrgsByAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city") String city) {

        return orgClient.getOrgsByAddress(apiVersion, province, city);
    }

    @RequestMapping(value = "distributeKey", method = RequestMethod.POST)
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode) {

        return orgClient.distributeKey(apiVersion, orgCode);
    }

    @RequestMapping(value = "/validation", method = RequestMethod.GET)
    public Object validationOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "api_version") String apiVersion,
                                @ApiParam(name = "orgCode", value = "机构代码")
                                @RequestParam(value = "orgCode") String orgCode) {

        Object object = orgClient.validationOrg(apiVersion, orgCode);
        return object;
    }
}
