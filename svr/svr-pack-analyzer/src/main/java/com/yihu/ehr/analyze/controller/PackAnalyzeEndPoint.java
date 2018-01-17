package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "AnalyzerEndPoint", description = "档案分析服务", tags = {"档案分析服务-档案分析"})
public class PackAnalyzeEndPoint extends EnvelopRestEndPoint {
}
