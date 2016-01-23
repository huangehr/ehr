package com.yihu.ehr.address.feignClient.address;

import com.yihu.ehr.model.address.MAddress;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-address")
//@RequestMapping(ApiVersionPrefix.CommonVersion + "/address")
@RequestMapping("/rest/v1.0/address")
public interface AddressClient {

    @RequestMapping(value = "/address", method = RequestMethod.GET ,consumes = "application/json")
    MAddress getAddressById(@RequestParam(value = "id") String id);


    @RequestMapping(value = "/canonicalAddress", method = RequestMethod.GET ,consumes = "application/json")
    String getCanonicalAddress(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/address", method =  RequestMethod.PUT,consumes = "application/json")
    String saveAddress(
            @RequestParam(value = "country") String country,
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district,
            @RequestParam(value = "town") String town,
            @RequestParam(value = "street") String street,
            @RequestParam(value = "extra") String extra,
            @RequestParam(value = "postalCode") String postalCode);


    @RequestMapping(value = "/search", method = RequestMethod.GET ,consumes = "application/json")
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district
    );

    @RequestMapping(value = "/address", method = RequestMethod.DELETE ,consumes = "application/json")
    void deleteByOrgCode(@RequestParam(value = "id") String id);


}
