package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.geogrephy.MGeography;
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
@RequestMapping(value = "/rest/v1.0")
public interface GeographyClient {

    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    MGeography getAddressById(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/geographies", method =  RequestMethod.PUT)
    String saveAddress(@RequestParam( value = "geography_model_json_data") String GeographyModelJsonData);


    @RequestMapping(value = "/geographies", method = RequestMethod.GET )
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district);
}
