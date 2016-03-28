package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.packs.MPackage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 9:27
 */
@ApiIgnore
@FeignClient(MicroServices.Package)
public interface XPackageMgrClient {
    @RequestMapping(value = ApiVersion.Version1_0 + RestApi.Packages.Package, method = RequestMethod.GET)
    MPackage acquirePackage();

    @RequestMapping(value = ApiVersion.Version1_0 + RestApi.Packages.Package, method = RequestMethod.PUT)
    void reportStatus(String id, ArchiveStatus status, String message);
}
