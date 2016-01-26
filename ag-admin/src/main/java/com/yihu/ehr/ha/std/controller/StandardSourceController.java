package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/stdSource")
@RestController
public class StandardSourceController extends BaseRestController {

    @RequestMapping(value = "/standardSource",method = RequestMethod.GET)
    public Object getStdSourceList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion) {
        return null;
    }


}
