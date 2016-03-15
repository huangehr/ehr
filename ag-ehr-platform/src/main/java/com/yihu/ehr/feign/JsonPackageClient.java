package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
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
@FeignClient(MicroServices.PackageMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface JsonPackageClient {
    @RequestMapping(value = "/packages", method = RequestMethod.PUT)
    void savePackageWithUser(
            @RequestParam(value = "file_string") String fileString,
            @RequestParam(value = "user_name") String userName,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5
    );

    @RequestMapping(value = "/packages", method = RequestMethod.POST)
    void savePackageWithOrg(
            @RequestParam(value = "file_string") String fileString,
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5
    );
}
