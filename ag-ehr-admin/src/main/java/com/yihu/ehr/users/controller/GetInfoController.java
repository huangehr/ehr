package com.yihu.ehr.users.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.users.service.GetInfoClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "userInfo", description = "获取用户可查询的机构&地区", tags = {"获取用户可查询的机构&地区"})
public class GetInfoController extends BaseController {

    @Autowired
    private GetInfoClient getInfoClient;

    @RequestMapping(value = "/userInfo/getOrgCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构代码")
    public String getOrgCode(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        List<String> list = getInfoClient.getOrgCode(userId);
        String orgCode = StringUtils.join(list, ",");
        return orgCode;
    }

    @RequestMapping(value = "/getOrgByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构名称")
    public Envelop getOrgByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        List<String> list = getInfoClient.getOrgByUserId(userId);
        return getResult(list,list.size(),1,999);
    }

    @RequestMapping(value = "/getDistrictByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的地区列表")
    public Envelop getDistrictByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        List<String> list = getInfoClient.getDistrictByUserId(userId);
        return getResult(list,list.size(),1,999);
    }

    @RequestMapping(value = "/getUserIdList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构下所对应的userId列表")
    public String getUserIdList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        List<String> list = getInfoClient.getUserIdList(userId);
        String userIdList = StringUtils.join(list, ",");
        return userIdList;
    }

    @RequestMapping(value = "/getDistrictList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构所对应的区域列表")
    public String getDistrictList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        List<String> list = getInfoClient.getDistrictList(userId);
        String districtList = StringUtils.join(list, ",");
        return districtList;
    }

    @RequestMapping(value = ServiceApi.GetInfo.GetAppIdsByUserId, method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户所在的角色组所对应的应用列表")
    public String getAppIdsByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        String appsId = getInfoClient.getAppIdsByUserId(userId);
        return appsId;
    }
}
