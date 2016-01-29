package com.yihu.ehr.ha.geography.service;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/1/20.
 */
@FeignClient("svr-address")
@RequestMapping("/rest/v1.0/address")
public interface AddressClient {

    @RequestMapping(value = "/address/level", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据地址等级查询地址信息")
    Object getAddressByLevel(@PathVariable(value = "api_version") String apiVersion,
                             @RequestParam(value = "level") Integer level);

    @RequestMapping(value = "/address/pid", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据父id查询地址信息")
    Object getAddressDictByPid(@PathVariable(value = "api_version") String apiVersion,
                               @RequestParam(value = "pid") Integer pid);

    @RequestMapping(value = "/address", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据id查询地址信息")
    Object getAddressById(@PathVariable(value = "api_version") String apiVersion,
                          @RequestParam(value = "id") String id);

    @RequestMapping(value = "/canonicalAddress", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据地址代码获取机构详细信息")
    Object getCanonicalAddress(@PathVariable(value = "api_version") String apiVersion,
                               @RequestParam(value = "id") String id);

    @RequestMapping(value = "/address", method = RequestMethod.PUT, consumes = "application/json")
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

    @RequestMapping(value = "/search", method = RequestMethod.GET, consumes = "application/json")
    @ApiOperation(value = "根据省市县查询地址")
    Object search(@PathVariable(value = "api_version") String apiVersion,
                  @RequestParam(value = "province") String province,
                  @RequestParam(value = "city") String city,
                  @RequestParam(value = "district") String district);

    @RequestMapping(value = "/address", method = RequestMethod.DELETE, consumes = "application/json")
    @ApiOperation(value = "根据id删除地址")
    Object delete(@PathVariable(value = "api_version") String apiVersion,
                  @RequestParam(value = "id") String id);
}
