package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.geography.MGeography;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@FeignClient(name = MicroServices.Geography)
public interface XGeographyClient {


    @RequestMapping(value = ApiVersion.Version1_0+"/geographies/{id}", method = RequestMethod.GET)
    MGeography getAddressById(@PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0+"/geographies", method =  RequestMethod.PUT)
    String saveAddress(@RequestParam(value = "json_data") String GeographyModelJsonData);


    @RequestMapping(value = ApiVersion.Version1_0+"/geographies", method = RequestMethod.GET )
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district);


    @RequestMapping(value = ApiVersion.Version1_0+"/geographies/existence", method = RequestMethod.GET )
    boolean isNullAddress(@RequestParam(value = "json_data") String geographyModelJsonData);

}
