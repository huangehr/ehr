package com.yihu.ehr.profiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * EHR 控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 11:06
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "EHR", description = "健康档案数据模型接口", tags = {"健康档案", "数据集", "HBase"})
public class PersonalProfilesController extends BaseRestController {

    @RequestMapping(value = "/profiles", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案列表", produces = "application/json", notes = "获取档案列表")
    public Object getProfiles(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "demographic_id", value = "患者人口学ID")
            @RequestParam(value = "demographic_id", required = true) String demographicId,
            @ApiParam(required = true, name = "from", value = "检索起始时间")
            @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @ApiParam(required = true, name = "to", value = "检索结束时间")
            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @ApiParam(required = true, name = "load_std_data_set", value = "是否加载标准数据集")
            @RequestParam(value = "load_std_data_set", required = true) boolean loadStdDataSet,
            @ApiParam(required = true, name = "load_origin_data_set", value = "是否加载原始数据集")
            @RequestParam(value = "load_origin_data_set", required = true) boolean loadOriginDataSet) {
        return  null;
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
        return  null;
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集", produces = "application/json", notes = "返回指定数据集对象，若key不存在，返回错误信息")
    public Object getDateSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "cda_version", required = true) String cda_version,
            @RequestParam(value = "data_set_code", required = true) String dataSetCode,
            @RequestParam(value = "row_keys", required = true) String rowKeyList) {
        return  null;
    }
}
