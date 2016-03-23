package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 20:14
 */
@FeignClient(name = MicroServices.Package)
@ApiIgnore
public interface JsonPackageClient {
    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Packages.LegacyPackages, method = RequestMethod.POST)
    void savePackageWithUser(
            @RequestParam(value = "file_string") String fileString,
            @RequestParam(value = "user_name") String userName,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5);

    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Packages.Packages, method = RequestMethod.POST)
    void savePackageWithOrg(
            @RequestParam(value = "file_string") String fileString,
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5);
}
