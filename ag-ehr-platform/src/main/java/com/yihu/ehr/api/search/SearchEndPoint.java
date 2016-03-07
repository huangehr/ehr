package com.yihu.ehr.api.search;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 平台搜索服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.24 18:34
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/search")
@Api(protocols = "https", value = "search", description = "搜索服务")
public class SearchEndPoint {
    @ApiOperation(value = "档案搜索", produces = "application/json", notes = "搜索健康档案")
    @RequestMapping(value = "/profiles", method = RequestMethod.GET)
    public Object searchProfiles(
            @ApiParam(required = true, name = "demographic_id", value = "患者人口学ID")
            @RequestParam(value = "demographic_id") String demographicId,
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

    @ApiOperation(value = "患者搜索", produces = "application/json", notes = "搜索患者信息")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Object searchPatients() {
        return null;
    }

    @ApiOperation(value = "组织机构搜索", produces = "application/json", notes = "组织机构搜索")
    @RequestMapping(value = "/organization", method = RequestMethod.GET)
    public Object searchOrganizations() {
        return null;
    }
}
