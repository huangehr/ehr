package com.yihu.ehr.api.pack;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.JsonPackageClient;
import com.yihu.ehr.util.encode.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

/**
 * 档案接收器。用于接收来自第三方应用的档案包。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.17 14:22
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/packages")
@Api(protocols = "https", value = "packages", description = "数据包服务")
public class PackagesEndPoint {

    @Autowired
    JsonPackageClient jsonPackageClient;

    /**
     * 归档病人档案-数据上传
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     * @param md5           文件内容MD5值。
     * @return
     */
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    @ApiOperation(value = "接收档案", notes = "接收健康档案数据包")
    public void receiveJsonPackage(
            @ApiParam(required = true, name = "package", value = "档案包", allowMultiple = true)
            MultipartHttpServletRequest jsonPackage,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(required = true, name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5) throws IOException {

        MultipartFile multipartFile = jsonPackage.getFile("file");
        byte[] bytes = multipartFile.getBytes();
        String fileString = Base64.encode(bytes);
        jsonPackageClient.savePackageWithUser(fileString, orgCode, packageCrypto, md5);
    }
}
