package com.yihu.ehr.profile.controller;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 9:45
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "健康档案模板服务", description = "维护/获取健康档案模板")
public class TemplateEndPoint {
}
