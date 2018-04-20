package com.yihu.ehr.basic.report.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.packs.EsDetailsPackage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

@ApiIgnore
@FeignClient(value = MicroServices.Package)
@RequestMapping(ApiVersion.Version1_0)
public interface PackMgrClient {

    @RequestMapping(value = ServiceApi.Packages.PackageSearch, method = RequestMethod.GET)
    Collection<EsDetailsPackage> packageList(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);
}
