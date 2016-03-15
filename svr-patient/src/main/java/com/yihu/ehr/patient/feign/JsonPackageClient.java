package com.yihu.ehr.patient.feign;

import com.yihu.ehr.constants.*;
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
@FeignClient(name = MicroServiceName.Package,url = MicroServiceIpAddressStr.Package+ MicroServicePort.Package)
@ApiIgnore
public interface JsonPackageClient {
    @RequestMapping(value = ApiVersion.Version1_0+"/package", method = POST)
    void savePackage(
//            MultipartHttpServletRequest jsonPackage,
            @RequestParam(value = "user_name") String userName,
            @RequestParam(value = "package_crypto") String packageCrypto,
            @RequestParam(value = "md5") String md5
    );

    @RequestMapping(value = ApiVersion.Version1_0+"/test", method = POST)
    void test();
}
