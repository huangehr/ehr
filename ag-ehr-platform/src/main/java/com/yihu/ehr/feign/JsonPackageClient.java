package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 20:14
 */
@FeignClient(name = MicroServiceName.Package)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface JsonPackageClient {
    @RequestMapping(value = "/packages", method = POST)
    void savePackageWithUser(
            @RequestParam(value = "file_string") String fileString,
            @RequestParam(value = "user_name") String userName,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5
    );

    @RequestMapping(value = "/packages", method = POST)
    void savePackageWithOrg(
            @RequestParam(value = "file_string") String fileString,
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5
    );
}
