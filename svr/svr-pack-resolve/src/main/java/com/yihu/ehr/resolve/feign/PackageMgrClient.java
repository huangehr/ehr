package com.yihu.ehr.resolve.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.profile.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.packs.EsDetailsPackage;
import com.yihu.ehr.model.packs.EsSimplePackage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 9:27
 */
@ApiIgnore
@FeignClient(name = MicroServices.Package)
@RequestMapping(ApiVersion.Version1_0)
public interface PackageMgrClient {

    @RequestMapping(value = ServiceApi.Packages.AcquirePackage, method = RequestMethod.GET)
    EsDetailsPackage acquirePackage(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.PUT)
    boolean reportStatus(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "status") ArchiveStatus status,
            @RequestParam(value = "errorTye") int errorTye,
            @RequestBody String message);

    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.GET)
    EsSimplePackage getPackage(
            @PathVariable(value = "id") String id);


    @RequestMapping(value = ServiceApi.Packages.ResolveMessage, method = RequestMethod.PUT)
    @Deprecated
    Map<String, String> sendResolveMessage(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "count", required = false) int count);

}
