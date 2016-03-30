package com.yihu.ehr.api.std;

import com.yihu.ehr.api.model.CDAVersionModel;
import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 18:05
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/standards")
@Api(value = "standards", description = "健康档案标准服务")
public class StandardsEndPoint {

    @ApiOperation(value = "获取健康档案数据标准版本", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = "/versions/{version_id}", method = {RequestMethod.GET})
    public CDAVersionModel getVersion(
            @ApiParam(name = "version_id", value = "版本ID，若为latest则返回最新版本", defaultValue = "latest")
            @PathVariable(value = "version_id")
            String versionId) {
        return null;
    }
}
