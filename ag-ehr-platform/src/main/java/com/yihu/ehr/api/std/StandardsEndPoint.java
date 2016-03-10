package com.yihu.ehr.api.std;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 18:05
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/standards")
@Api(protocols = "https", value = "standards", description = "健康档案标准服务")
public class StandardsEndPoint {
    @ApiOperation(value = "获取健康档案标准版本列表", produces = "application/json")
    @RequestMapping(value = "/versions", method = {RequestMethod.GET})
    public List<Object> getVersions() {
        return null;
    }

    @ApiOperation(value = "获取健康档案数据标准版本", produces = "application/json")
    @RequestMapping(value = "/versions/{version_id}", method = {RequestMethod.GET})
    public Object getVersion(
            @ApiParam(name = "version_id", value = "版本ID，若为latest则返回最新版本", defaultValue = "latest")
            @PathVariable(value = "version_id") String versionId) {
        return null;
    }
}
