package com.yihu.ehr.user.feignClient.org;

import com.yihu.ehr.model.org.MOrganization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-org")
public interface OrgClient {

    @RequestMapping(value = "/rest/{api_version}/org/", method = GET )
    MOrganization getOrg(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/rest/{api_version}/org/name", method = GET ,consumes = "application/json")
    List<String> getIdsByName(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "name") String name);

}
