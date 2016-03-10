package com.yihu.ehr.api.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 健康档案接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 11:06
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/patient/health_profiles")
@Api(protocols = "https", value = "health_profiles", description = "健康档案服务")
public class HealthProfilesEndPoint {
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/{demographic_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案列表", produces = "application/json", notes = "获取档案列表")
    public Object getProfiles(
            @ApiParam(required = true, name = "demographic_id", value = "患者人口学ID")
            @PathVariable(value = "demographic_id") String demographicId,
            @ApiParam(required = true, name = "from", value = "检索起始时间")
            @RequestParam(value = "since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(required = true, name = "to", value = "检索结束时间")
            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @ApiParam(required = true, name = "load_std_data_set", value = "是否加载标准数据集")
            @RequestParam(value = "load_std_data_set", required = true) boolean loadStdDataSet,
            @ApiParam(required = true, name = "load_origin_data_set", value = "是否加载原始数据集")
            @RequestParam(value = "load_origin_data_set", required = true) boolean loadOriginDataSet) {
        return null;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案", produces = "application/json", notes = "指定档案")
    public Object getProfile(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "archive_id", required = true) String archiveId,
            @ApiParam(required = true, name = "load_std_data_set", value = "是否加载标准数据集")
            @RequestParam(value = "load_std_data_set", required = true) boolean loadStdDataSet,
            @ApiParam(required = true, name = "load_origin_data_set", value = "是否加载原始数据集")
            @RequestParam(value = "load_origin_data_set", required = true) boolean loadOriginDataSet) {
        return null;
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集", produces = "application/json", notes = "返回指定数据集对象，若key不存在，返回错误信息")
    public Object getDateSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "cda_version", required = true) String cda_version,
            @RequestParam(value = "data_set_code", required = true) String dataSetCode,
            @RequestParam(value = "row_keys", required = true) String rowKeyList) {
        return null;
    }
}
