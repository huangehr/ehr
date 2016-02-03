package com.yihu.ehr.ha.geography.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/20.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.CommonVersion)
@RestController
@Api(value = "address", description = "地址信息管理接口，用于地址信息管理", tags = {"地址信息管理接口"})
public class AddressController extends BaseRestController {
    @Autowired
    private AddressClient addressClient;

    @RequestMapping(value = "/address/level", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址等级查询地址信息")
    public Object getAddressByLevel(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "level", value = "地址级别", defaultValue = "")
                                    @RequestParam(value = "level") Integer level) {

        return addressClient.getAddressByLevel(apiVersion, level);
    }

    @RequestMapping(value = "/address/pid", method = RequestMethod.GET)
    @ApiOperation(value = "根据父id查询地址信息")
    public Object getAddressDictByPid(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                      @ApiParam(name = "pid", value = "上级id", defaultValue = "")
                                      @RequestParam(value = "pid") Integer pid) {

        return addressClient.getAddressDictByPid(apiVersion, pid);
    }

    @RequestMapping(value = "/address/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址信息")
    public Object getAddressById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "api_version") String apiVersion,
                                 @ApiParam(name = "id", value = "地址编号", defaultValue = "")
                                 @RequestParam(value = "id") String id) {

        return addressClient.getAddressById(apiVersion, id);
    }

    @RequestMapping(value = "/address/canonical", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址代码获取机构详细信息")
    public Object getCanonicalAddress(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                      @ApiParam(name = "id", value = "地址代码", defaultValue = "")
                                      @RequestParam(value = "id") String id) {

        return addressClient.getCanonicalAddress(apiVersion, id);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    @ApiOperation(value = "地址检查并保存")
    public Object saveAddress(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "api_version") String apiVersion,
                              @ApiParam(name = "country", value = "国家", defaultValue = "")
                              @RequestParam(value = "country") String country,
                              @ApiParam(name = "province", value = "省", defaultValue = "")
                              @RequestParam(value = "province") String province,
                              @ApiParam(name = "city", value = "市", defaultValue = "")
                              @RequestParam(value = "city") String city,
                              @ApiParam(name = "district", value = "县", defaultValue = "")
                              @RequestParam(value = "district") String district,
                              @ApiParam(name = "town", value = "", defaultValue = "")
                              @RequestParam(value = "town") String town,
                              @ApiParam(name = "street", value = "街道", defaultValue = "")
                              @RequestParam(value = "street") String street,
                              @ApiParam(name = "extra", value = "其他", defaultValue = "")
                              @RequestParam(value = "extra") String extra,
                              @ApiParam(name = "postalCode", value = "行政编码", defaultValue = "")
                              @RequestParam(value = "postalCode") String postalCode) {

        return addressClient.saveAddress(apiVersion, country, province, city, district, town, street, extra, postalCode);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "根据省市县查询地址")
    public Object search(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                         @PathVariable(value = "api_version") String apiVersion,
                         @ApiParam(name = "province", value = "省", defaultValue = "")
                         @RequestParam(value = "province") String province,
                         @ApiParam(name = "city", value = "市", defaultValue = "")
                         @RequestParam(value = "city") String city,
                         @ApiParam(name = "district", value = "县", defaultValue = "")
                         @RequestParam(value = "district") String district) {

        return addressClient.search(apiVersion, province, city, district);
    }

    @RequestMapping(value = "/address/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    public Object delete(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                         @PathVariable(value = "api_version") String apiVersion,
                         @ApiParam(name = "/id", value = "地址代码", defaultValue = "")
                         @RequestParam(value = "id") String id) {

       return addressClient.delete(apiVersion,id);
    }
    @RequestMapping(value = "/address/is_address" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    public boolean isNullAddress(
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
            @RequestParam (value = "street") String street) {

        return  addressClient.isNullAddress(apiVersion,country,province,city,district,town,street);
    }

}
