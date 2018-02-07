package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.pack.PackageAnalyzeService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "AnalyzerEndPoint", description = "档案分析服务", tags = {"档案分析服务-档案分析"})
public class PackAnalyzeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PackageAnalyzeService packageAnalyzeService;

    @ApiOperation(value = "ES数据保存")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.EsSaveData, method = RequestMethod.POST)
    public Envelop esSaveData(
            @ApiParam(name = "index", value = "ES index")
            @RequestParam(value = "index", required = true) String index,
            @ApiParam(name = "type", value = "ES type")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "dataList", value = "上传的数据集")
            @RequestParam(value = "dataList", required = true) String dataList) {
        return packageAnalyzeService.esSaveData(index, type, dataList);
    }
}
