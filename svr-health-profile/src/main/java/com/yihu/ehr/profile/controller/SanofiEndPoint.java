package com.yihu.ehr.profile.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.profile.MProfile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 康赛（赛诺菲）项目患者体征数据提取API。体征数据包括：
 * - 体温、呼吸与脉搏
 * - 血液检验数据
 * - 尿液检验数据
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "赛诺菲数据服务", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, description = "赛诺菲项目体征数据提取服务")
public class SanofiEndPoint {
    @ApiOperation(value = "获取体征数据", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取体征数据")
    @RequestMapping(value = RestApi.HealthProfile.SanofiSupport, method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID")
            @PathVariable("id") String id) {
        return null;
    }
}
