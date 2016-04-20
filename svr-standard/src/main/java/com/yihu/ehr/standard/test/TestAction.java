package com.yihu.ehr.standard.test;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 惯用字典接口，用于快速提取常用的字典项。
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "测试", description = "测试")
public class TestAction extends BaseRestController {

    @RequestMapping(value = "/test/test", method = RequestMethod.GET)
    @ApiOperation(value = "测试", response = MConventionalDict.class)
    public String getAppCatalog(){
        return "测试";
    }


}
