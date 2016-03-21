package com.yihu.ehr.api.pack;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.JsonPackageClient;
import com.yihu.ehr.util.encode.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(ApiVersion.Version1_0)
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
    @RequestMapping(value = "/packages", method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "接收健康档案数据包")
    public ResponseEntity<Boolean> uploadPackage(
            @ApiParam(required = true, name = "package", value = "档案包", allowMultiple = true)
            MultipartHttpServletRequest jsonPackage,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(required = true, name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5) throws IOException {
        MultipartFile multipartFile = jsonPackage.getFile("file");

        //// TODO: 2016/3/18 大文件不能传 
//        HttpClient client = new HttpClient();



        byte[] bytes = multipartFile.getBytes();
        String fileString = Base64.encode(bytes);
        jsonPackageClient.savePackageWithOrg(fileString, orgCode, packageCrypto, md5);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }




}
