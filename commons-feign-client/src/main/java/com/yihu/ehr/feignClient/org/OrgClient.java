package com.yihu.ehr.feignClient.org;

import com.yihu.ehr.model.org.MOrganization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-org")
public interface OrgClient {
    @RequestMapping(value = "/org/org/{orgCode}", method = GET ,consumes = "application/json")
    MOrganization getOrg(
            @RequestParam(value = "orgCode") String orgCode);


}
