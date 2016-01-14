package com.yihu.ehr.user.user.service;

import com.yihu.ehr.model.org.OrganizationModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-org")
public interface OrganizationClient {


    @RequestMapping(value = "/org/org", method = GET ,consumes = "application/json")
    OrganizationModel getOrg(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/org/org/name", method = GET ,consumes = "application/json")
    List<String> getIdsByName(@RequestParam(value = "name") String name);

}
