package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.pack.PackageAnalyzeService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "AnalyzerEndPoint", description = "档案分析服务", tags = {"档案分析服务-档案分析"})
public class PackAnalyzeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PackageAnalyzeService packageAnalyzeService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

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

    @RequestMapping(value = "/packAnalyzer/updateStatus", method = RequestMethod.PUT)
    @ApiOperation(value = "根据条件批量修改档案包状态", notes = "修改档案包状态")
    public Integer update(
            @ApiParam(name = "filters", value = "条件", required = true)
            @RequestParam(value = "filters") String filters,
            @ApiParam(name = "status", value = "状态", required = true)
            @RequestParam(value = "status") String status,
            @ApiParam(name = "page", value = "page", required = true)
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "size", value = "size", required = true)
            @RequestParam(value = "size") Integer size) throws Exception {
        List<Map<String, Object>> sourceList = elasticSearchUtil.page("json_archives", "info", filters, page, size);
        List<Map<String, Object>> updateSourceList = new ArrayList<>(sourceList.size());
        sourceList.forEach(item -> {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", item.get("_id"));
            updateSource.put("analyze_status", status);
            updateSourceList.add(updateSource);
        });
        elasticSearchUtil.bulkUpdate("json_archives", "info", updateSourceList);
        return sourceList.size();
    }
}
