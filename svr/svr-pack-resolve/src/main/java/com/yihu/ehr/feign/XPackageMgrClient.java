package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.packs.MPackage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 9:27
 */
@ApiIgnore
@FeignClient(name = MicroServices.Package)
public interface XPackageMgrClient {
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Packages.AcquirePackage, method = RequestMethod.GET)
    MPackage acquirePackage(@RequestParam(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Packages.Package, method = RequestMethod.PUT)
    void reportStatus(@PathVariable(value = "id") String id,
                      @RequestParam(value = "status") ArchiveStatus status,
                      @RequestParam(value = "message") String message);


    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Packages.ResolveMessage, method = RequestMethod.PUT)
    Map<String, String> sendResolveMessage(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "count", required = false) int count);
}
