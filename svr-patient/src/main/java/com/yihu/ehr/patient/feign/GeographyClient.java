package com.yihu.ehr.patient.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.geogrephy.MGeography;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServiceName.Geography)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface GeographyClient {


    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    MGeography getAddressById(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/geographies", method =  RequestMethod.PUT)
    String saveAddress(@RequestParam( value = "json_data") String GeographyModelJsonData);


    @RequestMapping(value = "/geographies", method = RequestMethod.GET )
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district);


    @RequestMapping(value = "/geographies/existence", method = RequestMethod.GET )
    boolean isNullAddress(@RequestParam(value = "json_data") String geographyModelJsonData);

}
