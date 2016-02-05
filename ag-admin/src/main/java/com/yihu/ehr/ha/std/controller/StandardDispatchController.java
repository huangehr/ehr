package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/26.
 */

@RequestMapping(ApiVersionPrefix.Version1_0 + "/dispatch")
@RestController
public class StandardDispatchController extends BaseRestController {

    @RequestMapping(value = "/schema", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", produces = "application/json", notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    public Object getSchemeInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = true) String currentVersion) {
        return null;
    }
}
