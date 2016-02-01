package com.yihu.ehr.ha.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.organization.service.OrganizationClient;
import com.yihu.ehr.ha.users.model.UserModel;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/organization")
@RestController
public class OrganizationController extends BaseRestController {

    @Autowired
    private OrganizationClient orgClient;
    /**
     * 根据机构代码获取机构
     * @param orgCode 机构代码
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object getOrgDetailByCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode) {

        Object object = orgClient.getOrgByCode(apiVersion, orgCode);
        return object;
    }

    /**
     * 机构列表查询
     * @param settledWay 结算方式
     * @param orgType 机构类别
     * @param province 省
     * @param city 市
     * @param district 区
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "organizations", method = RequestMethod.GET)
    public Object getOrgs(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "fullName", value = "全称")
            @RequestParam(value = "fullName") String fullName,
            @ApiParam(name = "settledWay", value = "接入方式",defaultValue = "")
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
            @RequestParam(value = "rows") int rows){

        //TODO:查询机构列表
        return null;
    }

    /**
     * 删除机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "organization", method = RequestMethod.DELETE)
    public Object deleteOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode){

        //TODO:删除机构
        return null;
    }

    /**
     * 激活机构
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "activity" , method = RequestMethod.PUT)
    public Object activity(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "activityFlag", value = "状态", defaultValue = "")
            @RequestParam(value = "activityFlag") String activityFlag){

        //TODO:激活机构
        return null;
    }

    /**OrgModel
     * 跟新机构
     * @param orgModelJson
     * @return
     */
    @RequestMapping(value = "org" , method = RequestMethod.PUT)
    public Object updateOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            String orgModelJson) throws Exception{

        orgModelJson = URLDecoder.decode(orgModelJson, "UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        UserModel userModel = objectMapper.readValue(orgModelJson, UserModel.class);
        //TODO:修改机构数据
        return null;
    }

    /**
     * 根据name获取机构ids
     * @param name
     * @return
     */
    @RequestMapping(value = "/org/name", method = RequestMethod.GET)
    public Object getIdsByName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "机构名称", defaultValue = "")
            @RequestParam(value = "name") String name){
        return null;
    }
    @RequestMapping( value = "distributeKey" , method = RequestMethod.POST)
    public Object distributeKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode){

        return null;
    }
    /**
     * 根据地址获取机构
     * @param province
     * @param city
     * @return
     */
    @RequestMapping(value = "/orgs" , method = RequestMethod.GET)
    public Object getOrgs(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市")
            @RequestParam(value = "city") String city){

        return null;
    }

    @RequestMapping(value = "/isOrgCodeExist" ,method = RequestMethod.GET)
    public Object isOrgCodeExist(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode){

        //TODO:机构代码判断重复
        return null;
    }
}
