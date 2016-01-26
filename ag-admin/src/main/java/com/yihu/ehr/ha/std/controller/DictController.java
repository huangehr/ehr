package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/dict")
@RestController
public class DictController extends BaseRestController {

    @RequestMapping(value = "/dicts",method = RequestMethod.GET)
    public Object getDictsByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                       @PathVariable(value = "apiVersion") String apiVersion,
                                       @ApiParam(name = "versionCode", value = "标准版本代码")
                                       @RequestParam(value = "versionCode") String versionCode,
                                       @ApiParam(name = "code",value = "字典代码")
                                       @RequestParam(value = "code")String code,
                                       @ApiParam(name = "name",value = "字典名称")
                                       @RequestParam(value = "name")String name){
        return null;
    }
}
