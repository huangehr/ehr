package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/3/3.
 */
@FeignClient(name = MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface StandardDispatchClient {

    @RequestMapping(value = RestApi.Standards.Dispatches, method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", response = RestEcho.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    Object getSchemeInfo(
            @ApiParam(required = true, name = "userPrivateKey", value = "用户私钥")
            @RequestParam(value = "userPrivateKey", required = true) String userPrivateKey,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = false) String currentVersion);
}
