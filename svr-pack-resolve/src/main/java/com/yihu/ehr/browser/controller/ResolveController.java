package com.yihu.ehr.browser.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersion.Version1_0)
public class ResolveController {

    @ApiOperation("解析档案包")
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.PUT)
    public void doResolve(@ApiParam("id")
                          @PathVariable("id")
                          String packageId) {

    }
}
