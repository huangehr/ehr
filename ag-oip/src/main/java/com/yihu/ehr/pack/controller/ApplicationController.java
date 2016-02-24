package com.yihu.ehr.pack.controller;

import com.yihu.ehr.constants.ApiVersion;
import org.springframework.web.bind.annotation.*;

/**
 * 应用认证网关。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:53
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/apps")
public class ApplicationController {
    @RequestMapping(value = "/{appId}", method = RequestMethod.POST)
    public String getToken(
            @PathVariable String appId,
            @RequestParam String secret){
        return "";
    }
}
