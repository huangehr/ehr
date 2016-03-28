package com.yihu.ehr.browser.controller;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.16 11:46
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/profiles")
public class ProfilesEndPoint {

    @ApiOperation(value = "/search", notes = "档案搜索")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public void searchProfile() {
    }

    @ApiOperation(value = "/{demographic_id}", notes = "档案获取")
    @RequestMapping(value = "/{demographic_id}", method = RequestMethod.GET)
    public void getProfile(
            @ApiParam("身份证号")
            @PathVariable("demographic_id")
            String demographicId) {

    }
}
