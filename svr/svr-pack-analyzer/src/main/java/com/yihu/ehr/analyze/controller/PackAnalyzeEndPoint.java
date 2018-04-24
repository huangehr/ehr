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
    public boolean esSaveData(
            @ApiParam(name = "index", value = "ES index", required = true)
            @RequestParam(value = "index") String index,
            @ApiParam(name = "type", value = "ES type", required = true)
            @RequestParam(value = "type") String type,
            @ApiParam(name = "dataList", value = "上传的数据集", required = true)
            @RequestParam(value = "dataList") String dataList) throws Exception {
        packageAnalyzeService.esSaveData(index, type, dataList);
        return true;
    }
}
