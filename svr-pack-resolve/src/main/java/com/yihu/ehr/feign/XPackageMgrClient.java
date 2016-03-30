package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
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

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 9:27
 */
@ApiIgnore
@FeignClient(name = MicroServices.Package)
public interface XPackageMgrClient {
    @RequestMapping(value = ApiVersion.Version1_0 + RestApi.Packages.Package, method = RequestMethod.GET)
    MPackage acquirePackage(@PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + RestApi.Packages.Package, method = RequestMethod.PUT)
    void reportStatus(@PathVariable(value = "id") String id,
                      @RequestParam(value = "status") int status,
                      @RequestParam(value = "message") String message);
}
