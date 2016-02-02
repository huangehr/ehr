package com.yihu.ehr.pack.receiver;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.pack.feign.JsonPackageClient;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * JSON 档案接收器。用于接收来自集成开放平台的健康档案包。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.17 14:22
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "json-package-receiver", description = "Json数据包归档接口", tags = {"JSON", "数据集", "健康档案"})
public class JsonPackageReceiveController extends BaseRestController {
    @Autowired
    private JsonPackageClient jsonPackageClient;

    /**
     * 归档病人档案-数据上传
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     * @param md5 文件内容MD5值。
     *
     * @return
     */
    @RequestMapping(value = "/package", method = {RequestMethod.POST})
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    public void receiveJsonPackage(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,

            @ApiParam(required = true, name = "package", value = "JSON档案包", allowMultiple = true)
            MultipartHttpServletRequest jsonPackage,

            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name")
            String userName,

            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto")
            String packageCrypto,

            @ApiParam(required = true, name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5")
            String md5) {

        jsonPackageClient.savePackage(jsonPackage, userName, packageCrypto, md5);
    }
}
