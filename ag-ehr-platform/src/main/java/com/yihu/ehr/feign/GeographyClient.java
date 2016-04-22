package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.geography.MGeography;
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
@FeignClient(name = MicroServices.Geography)
@ApiIgnore
public interface GeographyClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/geographies/{id}", method = RequestMethod.GET)
    MGeography getAddressById(@PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + "/geographies", method = RequestMethod.POST)
    String saveAddress(@RequestParam(value = "json_data") String jsonData);

    @RequestMapping(value = ApiVersion.Version1_0 + "/geographies", method = RequestMethod.GET)
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district);
}
