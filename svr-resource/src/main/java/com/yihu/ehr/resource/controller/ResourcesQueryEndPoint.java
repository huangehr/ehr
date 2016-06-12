package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.service.ResourcesQueryService;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/query")
@Api(value = "query", description = "资源查询服务接口")
public class ResourcesQueryEndPoint {
    @Autowired
    private ResourcesQueryService resourcesQueryService;

    @Autowired
    private ResourcesQueryDao resourcesQueryDao;

    /**
     * 获取资源数据
     */
    @ApiOperation("获取资源数据")
    @RequestMapping(value = "/getResources", method = RequestMethod.GET)
    public Envelop getResources(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                @ApiParam("appId") @RequestParam(value = "appId", required = true) String appId,
                                @ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesQueryService.getResources(resourcesCode, appId, queryParams, page, size);
    }

    //-----------------------------给网关-----------------------------------//

    /**
     * 资源数据源结构
     */
    @ApiOperation("资源数据源结构")
    @RequestMapping(value = "/getResourceMetadata", method = RequestMethod.GET)
    public String getResourceMetadata(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode) throws Exception {
        return resourcesQueryService.getResourceMetadata(resourcesCode);
    }

    /**
     * 资源浏览
     */
    @ApiOperation("资源浏览")
    @RequestMapping(value = "/getResourceData", method = RequestMethod.GET)
    public Envelop getResourceData(@ApiParam(name = "resourcesCode", defaultValue = "RS_HOSPITALIZED_DIAGNOSIS") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                   @ApiParam("queryCondition") @RequestParam(value = "queryCondition", required = false) String queryCondition,
                                   @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryService.getResourceData(resourcesCode, queryCondition, page, size);
    }

    @ApiOperation("内部--Hbase主表")
    @RequestMapping(value = "/getEhrCenter", method = RequestMethod.GET)
    public Page<Map<String, Object>> getEhrCenter(@ApiParam(name = "queryParams", defaultValue = "{\"q\":\"demographic_id:420521195812172917\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                  @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                  @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryDao.getEhrCenter(queryParams, page, size);
    }

    @ApiOperation("内部--Hbase从表")
    @RequestMapping(value = "/getEhrCenterSub", method = RequestMethod.GET)
    public Envelop getEhrCenterSub(
            @ApiParam(name = "queryParams", defaultValue = "{\"table\":\"HDSC02_17\",\"join\":\"demographic_id:420521195812172917\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getEhrCenterSub(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }

    @ApiOperation("内部--Hbase主表统计")
    @RequestMapping(value = "/countEhrCenter", method = RequestMethod.GET)
    public Page<Map<String, Object>> countEhrCenter(@ApiParam(value = "queryParams", defaultValue = "{\"groupFields\":\"org_code,HDSD00_01_003_VALUE_S,HDSA00_01_015_VALUE_S\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryDao.countEhrCenter(queryParams, page, size);
    }

    @ApiOperation("内部--Hbase从表统计")
    @RequestMapping(value = "/countEhrCenterSub", method = RequestMethod.GET)
    public Page<Map<String, Object>> countEhrCenterSub(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                       @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                       @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryDao.countEhrCenterSub(queryParams, page, size);
    }

    @ApiOperation("内部--Mysql查询")
    @RequestMapping(value = "/getMysqlData", method = RequestMethod.GET)
    public Page<Map<String, Object>> getMysqlData(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                  @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                  @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryDao.getMysqlData(queryParams, page, size);
    }

    @ApiOperation("EHR内部标准转国家标准")
    @RequestMapping(value="/stdlisttransform",method = RequestMethod.POST)
    public List<Map<String,Object>> convertDisplayCodes(
            @ApiParam(name="resources",value="资源",required = true)
            @RequestParam(value = "resources",required = true)
                    String resources,
            @ApiParam(name="version",value="版本",required = true)
            @RequestParam(value = "version",required = true)
                    String version)
    {
        JSONArray dataArray = new JSONArray(resources);
        Iterator iterator = dataArray.iterator();
        List<Map<String,Object>> rsData = new ArrayList<Map<String, Object>>();

        while(iterator.hasNext())
        {
            JSONObject object = (JSONObject)iterator.next();
            Map<String,Object> map = new HashMap<String,Object>();

            for(String key : object.keySet())
            {
                map.put(key,object.get(key));
            }

            rsData.add(map);
        }

        return resourcesQueryService.displayCodeConvert(rsData,version);
    }

    @ApiOperation("EHR内部标准转国家标准")
    @RequestMapping(value="/stdtransform",method = RequestMethod.POST)
    public List<Map<String,Object>> convertDisplayCode(
            @ApiParam(name="resource",value="资源",required = true)
            @RequestParam(value = "resource",required = true)
                    String resource,
            @ApiParam(name="version",value="版本",required = true)
            @RequestParam(value = "version",required = true)
                    String version)
    {
        List<Map<String,Object>> rsData = new ArrayList<Map<String, Object>>();
        JSONObject object = new JSONObject(resource);
        Map<String,Object> map = new HashMap<String,Object>();

        for(String key : object.keySet())
        {
            map.put(key,object.get(key));
        }

        rsData.add(map);

        return resourcesQueryService.displayCodeConvert(rsData,version);
    }
}
