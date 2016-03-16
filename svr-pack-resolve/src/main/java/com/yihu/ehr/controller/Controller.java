package com.yihu.ehr.controller;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersion.Version1_0 + "/packages")
public class Controller {

    @ApiOperation("解析档案包")
    @RequestMapping(value = "/{package_id}", method = RequestMethod.PUT)
    public void doResolve(@ApiParam("package_id")
                          @PathVariable("package_id")
                          String packageId){

    }
}
