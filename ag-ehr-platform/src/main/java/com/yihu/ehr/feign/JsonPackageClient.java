package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.*;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 20:14
 */
@FeignClient(name = MicroServices.PackageMgr,url = MicroServiceIpAddressStr.Package+MicroServicePort.Package)
@ApiIgnore
public interface JsonPackageClient {
    @RequestMapping(value = ApiVersion.Version1_0+RestApi.Packages.Packages, method = RequestMethod.PUT)
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
