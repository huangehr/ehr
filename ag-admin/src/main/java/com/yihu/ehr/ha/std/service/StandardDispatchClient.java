package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/3/3.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface StandardDispatchClient {

    @RequestMapping(value = "/standard-dispatcher/schema", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", produces = "application/json",
            notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    RestEcho getSchemeInfo(
            @ApiParam(required = true, name = "userPrivateKey", value = "用户私钥")
            @RequestParam(value = "userPrivateKey", required = true) String userPrivateKey,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = true) String currentVersion);
}
