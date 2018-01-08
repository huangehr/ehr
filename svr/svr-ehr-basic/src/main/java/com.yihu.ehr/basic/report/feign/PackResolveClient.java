package com.yihu.ehr.basic.report.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@FeignClient(value = MicroServices.PackageResolve)
@RequestMapping(ApiVersion.Version1_0)
public interface PackResolveClient {

    @RequestMapping(value = "/packages/fetch/{id}", method = RequestMethod.GET)
    ResponseEntity<String> fetch(
            @PathVariable(value = "id") String id);
}
