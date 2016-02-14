package com.yihu.ehr.patient.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.address.MGeography;
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


    @RequestMapping(value = "/rest/{api_version}/geographies/{id}", method = RequestMethod.GET)
    MGeography getAddressById(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/rest/{api_version}/geographies", method =  RequestMethod.PUT)
    String saveAddress(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam( value = "geography_model_json_data") String GeographyModelJsonData);


    @RequestMapping(value = "/rest/{api_version}/geographies/{province}/{city}/{district}", method = RequestMethod.GET )
    List<String> search(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "province") String province,
            @PathVariable(value = "city") String city,
            @PathVariable(value = "district") String district);


    @RequestMapping(value = "/rest/{api_version}/geographies/existence", method = RequestMethod.GET )
    Boolean isNullAddress(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "geography_model_json_data") String geographyModelJsonData);

}
