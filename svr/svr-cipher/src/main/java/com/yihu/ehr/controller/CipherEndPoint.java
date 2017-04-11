package com.yihu.ehr.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.ICipherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 加密解密服务接口
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "cipher", description = "加密解密接口")
public class CipherEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ICipherService cipherService; //加密解密服务

    @RequestMapping(value= ServiceApi.Cipher.Encryption,method = RequestMethod.GET)
    @ApiOperation("加密")
    public String encrypt(
            @ApiParam(name="type",value="加密类型",defaultValue = "des")
            @PathVariable(value="type")String type,
            @ApiParam(name="plainText",value="明文数据",defaultValue = "")
            @RequestParam(value="plainText")String plainText) throws Exception
    {
        return cipherService.encrypt(plainText,type);
    }

    @RequestMapping(value=ServiceApi.Cipher.Decryption,method = RequestMethod.GET)
    @ApiOperation("解密")
    public String decrypt(
            @ApiParam(name="type",value="解密类型",defaultValue = "des")
            @PathVariable(value="type")String type,
            @ApiParam(name="cipherText",value="密文数据",defaultValue = "")
            @RequestParam(value="cipherText")String cipherText) throws Exception
    {
        return cipherService.decrypt(cipherText,type);
    }
}
