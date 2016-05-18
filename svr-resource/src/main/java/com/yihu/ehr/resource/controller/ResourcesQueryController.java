package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.service.intf.IResourcesQueryService;
import com.yihu.ehr.resource.service.intf.IResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/query")
@Api(value = "query", description = "资源查询服务接口")
public class ResourcesQueryController {
    @Autowired
    private IResourcesQueryService resourcesQueryService;

    @Autowired
    private ResourcesQueryDao resourcesQueryDao;

    /**
     *获取资源数据
     */
    @ApiOperation("获取资源数据")
    @RequestMapping(value = "/getResources", method = RequestMethod.GET)
    public DataList getResources(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                 @ApiParam("appId") @RequestParam(value = "appId", required = true) String appId,
                                 @ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                 @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                 @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesQueryService.getResources(resourcesCode,appId,queryParams,page,size);
    }


    @ApiOperation("测试--Hbase主表")
    @RequestMapping(value = "/getEhrCenter", method = RequestMethod.GET)
    public DataList getEhrCenter(@ApiParam(name="queryParams",defaultValue="{\"q\":\"demographic_id:412726196106128414\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                 @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                 @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesQueryDao.getEhrCenter(queryParams,page,size);
    }

    @ApiOperation("测试--Hbase从表")
    @RequestMapping(value = "/getEhrCenterSub", method = RequestMethod.GET)
    public DataList getEhrCenterSub(@ApiParam(name="queryParams",defaultValue="{\"table\":\"HDSD00_08\",\"q\":\"main_rowkey:41872607-9_10289499_000618952_1443715200000\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                 @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                 @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesQueryDao.getEhrCenterSub(queryParams, page, size);
    }

    @ApiOperation("测试--Hbase主表统计")
    @RequestMapping(value = "/countEhrCenter", method = RequestMethod.GET)
    public DataList countEhrCenter(@ApiParam(value = "queryParams",defaultValue = "{\"groupFields\":\"org_code,HDSD00_01_003_VALUE_S,HDSA00_01_015_VALUE_S\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                 @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                 @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesQueryDao.countEhrCenter(queryParams, page, size);
    }

    @ApiOperation("测试--Hbase从表统计")
    @RequestMapping(value = "/countEhrCenterSub", method = RequestMethod.GET)
    public DataList countEhrCenterSub(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                   @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesQueryDao.countEhrCenterSub(queryParams, page, size);
    }

    @ApiOperation("测试--Mysql查询")
    @RequestMapping(value = "/getMysqlData", method = RequestMethod.GET)
    public DataList getMysqlData(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                      @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                      @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesQueryDao.getMysqlData(queryParams, page, size);
    }
}
