package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsDimension;
import com.yihu.ehr.resource.client.DimensionClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "dimensions", description = "维度服务接口")
public class DimensionController extends BaseController {

    @Autowired
    private DimensionClient dimensionClient;

    @ApiOperation("创建维度")
    @RequestMapping(value = "/dimensions",method = RequestMethod.POST)
    public MRsDimension createDimension(
            @ApiParam(name = "dimension", value = "维度", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {
        return dimensionClient.createDimension(dimension);
    }

    @ApiOperation("更新维度")
    @RequestMapping(value = "/dimensions",method = RequestMethod.PUT)
    public MRsDimension updateDimension(
            @ApiParam(name = "dimension", value = "维度", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {
        return dimensionClient.updateDimension(dimension);
    }

    @ApiOperation("维度删除")
    @RequestMapping(value = "/dimensions/{id}", method = RequestMethod.DELETE)
    public boolean deleteDimension(
            @ApiParam(name = "id", value = "维度ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return dimensionClient.deleteDimension(id);
    }

    @ApiOperation("维度查询")
    @RequestMapping(value = "/dimensions", method = RequestMethod.GET)
    public Page<MRsDimension> queryDimensions(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        return dimensionClient.queryDimensions(fields,filters,sorts,page,size);
    }
}
