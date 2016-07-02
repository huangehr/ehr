package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.service.ResourcesQueryService;
import com.yihu.ehr.resource.service.ResourcesTransformService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "query", description = "资源查询服务接口")
public class ResourcesQueryEndPoint {
    @Autowired
    ResourcesQueryService resourcesQueryService;

    @Autowired
    ResourcesQueryDao resourcesQueryDao;

    @Autowired
    ResourcesTransformService resourcesTransformService;

    /*********************************** 公开接口 ************************************************************/
    /**
     * 获取资源数据
     */
    @ApiOperation("获取资源数据")
    @RequestMapping(value = ServiceApi.Resources.ResourcesQuery, method = RequestMethod.POST)
    public Envelop getResources(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                @ApiParam("appId") @RequestParam(value = "appId", required = true) String appId,
                                @ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesQueryService.getResources(resourcesCode, appId, queryParams, page, size);
    }

    /**
     * 获取资源数据(转译)
     */
    @ApiOperation("获取资源数据(转译)")
    @RequestMapping(value = ServiceApi.Resources.ResourcesQueryTransform, method = RequestMethod.POST)
    public Envelop getResourcesTransform(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                @ApiParam("appId") @RequestParam(value = "appId", required = true) String appId,
                                @ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("size") @RequestParam(value = "size", required = false) Integer size,
                                @ApiParam("version") @RequestParam(value = "version", required = false) String version) throws Exception {

        Envelop re = resourcesQueryService.getResources(resourcesCode, appId, queryParams, page, size);
        if(version!=null && version.length()>0)
        {
            List<Map<String,Object>> list = re.getDetailModelList();
            re.setDetailModelList(resourcesTransformService.displayCodeConvert(list,version,null));
        }

        return re;
    }

    /**
     * 资源数据源结构
     */
    @ApiOperation("资源数据源结构")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewMetadata, method = RequestMethod.GET)
    public String getResourceMetadata(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode) throws Exception {
        return resourcesQueryService.getResourceMetadata(resourcesCode);
    }

    /**
     * 资源浏览
     */
    @ApiOperation("资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewData, method = RequestMethod.GET)
    public Envelop getResourceData(@ApiParam(name = "resourcesCode", defaultValue = "RS_HOSPITALIZED_DIAGNOSIS") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                   @ApiParam("queryCondition") @RequestParam(value = "queryCondition", required = false) String queryCondition,
                                   @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryService.getResourceData(resourcesCode, queryCondition, page, size);
    }

    /**
     * 获取非结构化数据
     */
    @ApiOperation("获取非结构化数据")
    @RequestMapping(value = ServiceApi.Resources.ResourcesRawFiles, method = RequestMethod.GET)
    public Envelop getRawFiles(@ApiParam("profileId") @RequestParam(value = "profileId", required = true) String profileId,
                               @ApiParam("cdaDocumentId") @RequestParam(value = "cdaDocumentId", required = false) String cdaDocumentId,
                                @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesQueryService.getRawFiles(profileId,cdaDocumentId,page,size);
    }

    @ApiOperation("Hbase主表")
    @RequestMapping(value = ServiceApi.Resources.ResourcesMasterData, method = RequestMethod.GET)
    public Envelop getEhrCenter(@ApiParam(name = "queryParams", defaultValue = "{\"q\":\"*:*\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("size") @RequestParam(value = "size", required = false) Integer size,
                                @ApiParam("version") @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getEhrCenter(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        if(version!=null && version.length()>0)
        {
            re.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(),version,null));
        }
        else{
            re.setDetailModelList(result.getContent());
        }
        return re;
    }

    @ApiOperation("Hbase从表")
    @RequestMapping(value = ServiceApi.Resources.ResourcesSubData, method = RequestMethod.GET)
    public Envelop getEhrCenterSub(
            @ApiParam(name = "queryParams", defaultValue = "{\"table\":\"HDSC02_17\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size,
            @ApiParam("version") @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getEhrCenterSub(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        if(version!=null && version.length()>0)
        {
            re.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(),version,null));
        }
        else{
            re.setDetailModelList(result.getContent());
        }
        return re;
    }


    @ApiOperation("Hbase主表统计")
    @RequestMapping(value = ServiceApi.Resources.ResourcesMasterStat, method = RequestMethod.GET)
    public Envelop countEhrCenter(@ApiParam(value = "queryParams", defaultValue = "{\"groupFields\":\"org_code,HDSD00_01_003_VALUE_S,HDSA00_01_015_VALUE_S\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.countEhrCenter(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }

    @ApiOperation("Hbase从表统计")
    @RequestMapping(value = ServiceApi.Resources.ResourcesSubStat, method = RequestMethod.GET)
    public Envelop countEhrCenterSub(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                       @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                       @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.countEhrCenterSub(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }

    @ApiOperation("Mysql查询")
    @RequestMapping(value = ServiceApi.Resources.ResourcesMysql, method = RequestMethod.GET)
    public Envelop getMysqlData(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                  @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                  @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getMysqlData(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }


}
