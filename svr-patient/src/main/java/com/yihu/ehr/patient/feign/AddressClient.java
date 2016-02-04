package com.yihu.ehr.patient.feign;

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
@RequestMapping("/rest/v1.0/address")
public interface AddressClient {

    @RequestMapping(value = "", method = RequestMethod.GET )
    MAddress getAddressById(@RequestParam(value = "id") String id);


    @RequestMapping(value = "/canonical", method = RequestMethod.GET)
    String getCanonicalAddress(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "", method =  RequestMethod.PUT)
    String saveAddress(
            @RequestParam(value = "country") String country,
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district,
            @RequestParam(value = "town") String town,
            @RequestParam(value = "street") String street,
            @RequestParam(value = "extra") String extra,
            @RequestParam(value = "postalCode") String postalCode);


    @RequestMapping(value = "/search", method = RequestMethod.GET)
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district);

    @RequestMapping(value = "", method = RequestMethod.DELETE )
    void delete(@RequestParam(value = "id") String id);


    @RequestMapping(value = "/is_address", method = RequestMethod.GET )
    Boolean isNullAddress(
            @RequestParam(value = "country") String country,
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district,
            @RequestParam(value = "town") String town,
            @RequestParam(value = "street") String street);

}
