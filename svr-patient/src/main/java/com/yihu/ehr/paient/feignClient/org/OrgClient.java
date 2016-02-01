package com.yihu.ehr.paient.feignClient.org;

import com.yihu.ehr.model.org.MOrganization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-org")
@RequestMapping("/rest/v1.0/org")
public interface OrgClient {

    @RequestMapping(value = "/org", method = GET ,consumes = "application/json")
    MOrganization getOrg(@RequestParam(value = "orgCode") String orgCode);

    @RequestMapping(value = "/org/name", method = GET ,consumes = "application/json")
    List<String> getIdsByName(@RequestParam(value = "name") String name);

}
