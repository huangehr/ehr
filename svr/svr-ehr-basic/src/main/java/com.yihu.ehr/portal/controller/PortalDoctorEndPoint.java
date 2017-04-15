package com.yihu.ehr.portal.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "portal", description = "医生端云门户", tags = {"云门户-医生端云门户"})
public class PortalDoctorEndPoint extends EnvelopRestEndPoint {

    @RequestMapping(value = "/doctor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "医生端云门户")
    public String createAppApi(
            @ApiParam(name = "json", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appApiJson) throws Exception {
        String result = "success";
        return result;
    }
}
