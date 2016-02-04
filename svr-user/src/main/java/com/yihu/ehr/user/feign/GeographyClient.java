package com.yihu.ehr.user.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.address.MAddress;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.Geography)
public interface GeographyClient {

    @RequestMapping(value = "/rest/{api_version}/geography", method = RequestMethod.GET )
    MAddress getAddressById(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "id") String id);


    @RequestMapping(value = "/rest/{api_version}/geography/canonical", method = RequestMethod.GET )
    String getCanonicalAddress(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/geography", method =  RequestMethod.PUT,consumes = "application/json")
    String saveAddress(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "country") String country,
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district,
            @RequestParam(value = "town") String town,
            @RequestParam(value = "street") String street,
            @RequestParam(value = "extra") String extra,
            @RequestParam(value = "postalCode") String postalCode);


    @RequestMapping(value = "/rest/{api_version}/geography/search", method = RequestMethod.GET )
    List<String> search(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district
    );


}
