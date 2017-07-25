package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface GetInfoClient {

    @RequestMapping(value = "/userInfo/getOrgCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构代码")
    List<String> getOrgCode(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getOrgByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构名称")
    List<String> getOrgByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getDistrictByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的地区")
    List<String> getDistrictByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getUserIdList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构下所对应的userId列表")
    List<String> getUserIdList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getDistrictList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户可查询的机构所对应的区域列表")
    List<String> getDistrictList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);
}
