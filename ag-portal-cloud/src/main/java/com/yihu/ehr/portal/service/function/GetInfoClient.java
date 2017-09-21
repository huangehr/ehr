package com.yihu.ehr.portal.service.function;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
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
    @ApiOperation(value = "获取当前用户的机构代码")
    List<String> getOrgCode(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getOrgByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户的机构名称")
    List<String> getOrgByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getDistrictByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户管理的地区")
    List<String> getDistrictByUserId(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/userInfo/getUserDistrictCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户所属区域可查询的代码")
    List<String> getUserDistrictCode(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getUserIdList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户管理的机构所对应的userId列表")
    List<String> getUserIdList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/getDistrictList", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前用户管理的机构所对应的 区域列表")
    List<String> getDistrictList(
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);
}
