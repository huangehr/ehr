package com.yihu.ehr.security.service;

import com.yihu.ehr.model.AddressModel;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-user")
public interface AppClient {

    @RequestMapping(value = "/address/getAddressById", method = GET ,consumes = "application/json")
    AddressModel getAddressById(
            @RequestParam(value = "id") String id);


    @RequestMapping(value = "/address/canonicalAddress", method = GET ,consumes = "application/json")
    String getCanonicalAddress(
            @RequestParam(value = "id") String id);



    @RequestMapping(value = "/address/search", method = GET ,consumes = "application/json")
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district
    );

    @RequestMapping(value = "/address/delete", method = GET ,consumes = "application/json")
    void deleteByOrgCode(@ApiParam(name = "/id", value = "地址代码", defaultValue = "")
                         @RequestParam(value = "id") String id);
}
