package com.yihu.ehr.ha.geography.service;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/1/20.
 */
@FeignClient("svr-address")
public interface AddressClient {

    @RequestMapping(value = "/rest/{api_version}/address/level", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据地址等级查询地址信息")
    Object getAddressByLevel(@PathVariable(value = "api_version") String apiVersion,
                             @RequestParam(value = "level") Integer level);

    @RequestMapping(value = "/rest/{api_version}/address/pid", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据父id查询地址信息")
    Object getAddressDictByPid(@PathVariable(value = "api_version") String apiVersion,
                               @RequestParam(value = "pid") Integer pid);

    @RequestMapping(value = "/rest/{api_version}/address/", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据id查询地址信息")
    Object getAddressById(@PathVariable(value = "api_version") String apiVersion,
                          @RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/address/canonical", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据地址代码获取机构详细信息")
    Object getCanonicalAddress(@PathVariable(value = "api_version") String apiVersion,
                               @RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/address/", method = RequestMethod.PUT, consumes = "application/json")
    @ApiOperation(value = "地址检查并保存")
    Object saveAddress(@PathVariable(value = "api_version") String apiVersion,
                       @RequestParam(value = "country") String country,
                       @RequestParam(value = "province") String province,
                       @RequestParam(value = "city") String city,
                       @RequestParam(value = "district") String district,
                       @RequestParam(value = "town") String town,
                       @RequestParam(value = "street") String street,
                       @RequestParam(value = "extra") String extra,
                       @RequestParam(value = "postalCode") String postalCode);

    @RequestMapping(value = "/rest/{api_version}/address/search", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据省市县查询地址")
    Object search(@PathVariable(value = "api_version") String apiVersion,
                  @RequestParam(value = "province") String province,
                  @RequestParam(value = "city") String city,
                  @RequestParam(value = "district") String district);

    @RequestMapping(value = "/rest/{api_version}/address/", method = RequestMethod.DELETE, consumes = "application/json")
    @ApiOperation(value = "根据id删除地址")
    Object delete(@PathVariable(value = "api_version") String apiVersion,
                  @RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/address/is_address" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    boolean isNullAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "/country" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "country") String country,
            @ApiParam(name = "/province" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "province") String province,
            @ApiParam(name = "/city" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "city") String city,
            @ApiParam(name = "/district" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "district") String district,
            @ApiParam(name = "/town" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "town") String town,
            @ApiParam(name = "/street" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "street") String street) ;
}
