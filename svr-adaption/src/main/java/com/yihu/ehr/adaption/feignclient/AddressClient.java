package com.yihu.ehr.adaption.feignclient;


import com.yihu.ehr.model.address.MAddress;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Created by Administrator on 2016/1/4.
 */
@EnableFeignClients
@FeignClient("svr-address")
@RequestMapping("/rest")
public interface AddressClient {

    @RequestMapping(value = "/{api_version}/address/address", method = RequestMethod.PUT)
    @ApiOperation(value = "地址检查并保存")
    public Object saveAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "country", value = "国家" , defaultValue = "")
            @RequestParam(value = "country") String country,
            @ApiParam(name = "province", value = "省" , defaultValue = "")
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
            @RequestParam(value = "postalCode") String postalCode);

}
