package com.yihu.ehr.pack.feign;

import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 20:14
 */
@FeignClient(MicroServices.PackageMgr)
public interface JsonPackageClient {
    @RequestMapping(value = "/rest/v1.0/package", method = POST)
    void savePackage(MultipartHttpServletRequest jsonPackage,
                     @RequestParam(value = "user_name")
                     String userName,
                     @RequestParam(value = "package_crypto")
                     String packageCrypto,
                     @RequestParam(value = "md5")
                     String md5
    );
}
